/*
 * This file is a modified class of dnsjava, an implementation of the dns protocol in java.
 * Licensed under the BSD-3-Clause.
 */
package arc.net.dns;

import static arc.net.dns.ArcDns.dnsResolverPort;

import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.util.StringTokenizer;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.OS;

public final class ResolvConfNameserverProvider implements NameserverProvider{

    @Override
    public Seq<InetSocketAddress> getNameservers(){
        Seq<InetSocketAddress> out = new Seq<>();
        // first try the default unix config path
        if(!tryParseResolveConf("/etc/resolv.conf", out)){
            // then fallback to netware
            tryParseResolveConf("sys:/etc/resolv.cfg", out);
        }
        return out;
    }

    private boolean tryParseResolveConf(String path, Seq<InetSocketAddress> out){
        Fi conf = new Fi(path);

        if(conf.exists()){
            try(BufferedReader reader = conf.reader(1024)){
                String line;
                while((line = reader.readLine()) != null){
                    StringTokenizer tokenizer = new StringTokenizer(line);
                    if(!tokenizer.hasMoreTokens()) continue;

                    if(tokenizer.nextToken().equals("nameserver")){
                        out.add(new InetSocketAddress(tokenizer.nextToken(), dnsResolverPort));
                    }
                }
                return true;
            }catch(Exception ignored){
            }
        }

        return false;
    }

    @Override
    public boolean isEnabled(){
        return !OS.isWindows && !OS.isAndroid;
    }
}
