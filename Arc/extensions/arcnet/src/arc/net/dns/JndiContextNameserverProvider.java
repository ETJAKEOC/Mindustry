/*
 * This file is a modified class of dnsjava, an implementation of the dns protocol in java.
 * Licensed under the BSD-3-Clause.
 */
package arc.net.dns;

import static arc.net.dns.ArcDns.dnsResolverPort;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import arc.struct.Seq;
import arc.util.Log;
import arc.util.OS;

/**
 * Resolver config provider that tries to extract the system's DNS servers from the
 * <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jndi/jndi-dns.html">JNDI DNS Service Provider</a>.
 */
public final class JndiContextNameserverProvider implements NameserverProvider{

    @Override
    public Seq<InetSocketAddress> getNameservers(){
        try{
            return new Inner().getNameservers();
        }catch(Throwable t){
            return new Seq<>();
        }
    }

    @Override
    public boolean isEnabled(){
        return !OS.isAndroid && !OS.isIos;
    }

    //loading a class fails if it refers to unknown classes (javax.naming is not available on some platforms), so put it in a separate inner class
    static class Inner implements NameserverProvider{

        @Override
        public Seq<InetSocketAddress> getNameservers(){
            Seq<InetSocketAddress> result = new Seq<>();

            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
            // http://mail.openjdk.java.net/pipermail/net-dev/2017-March/010695.html
            env.put("java.naming.provider.url", "dns://");

            String servers = null;
            try{
                DirContext ctx = new InitialDirContext(env);
                servers = (String)ctx.getEnvironment().get("java.naming.provider.url");
                ctx.close();
            }catch(NamingException ignored){
            }

            if(servers != null){
                StringTokenizer st = new StringTokenizer(servers, " ");
                while(st.hasMoreTokens()){
                    String server = st.nextToken();
                    try{
                        URI serverUri = new URI(server);
                        String host = serverUri.getHost();
                        if(host == null || host.isEmpty()){
                            // skip the fallback server to localhost
                            continue;
                        }

                        int port = serverUri.getPort();
                        if(port == -1){
                            port = dnsResolverPort;
                        }

                        result.add(new InetSocketAddress(host, port));
                    }catch(URISyntaxException e){
                        Log.debug("[DNS] Could not parse @ as a dns server, ignoring: @", server, e);
                    }
                }
            }

            return result;
        }
    }
}
