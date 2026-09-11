/*
 * This file is a modified class of dnsjava, an implementation of the dns protocol in java.
 * Licensed under the BSD-3-Clause.
 */
package arc.net.dns;

import java.net.InetSocketAddress;

import arc.struct.Seq;

public interface NameserverProvider{
    /** Returns all located servers, which may be empty. */
    Seq<InetSocketAddress> getNameservers();

    /** Determines if this provider is enabled. */
    default boolean isEnabled(){
        return true;
    }
}
