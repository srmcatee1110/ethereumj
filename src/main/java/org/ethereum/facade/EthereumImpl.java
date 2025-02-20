package org.ethereum.facade;

import org.ethereum.core.Block;
import org.ethereum.listener.EthereumListener;
import org.ethereum.manager.WorldManager;
import org.ethereum.net.client.ClientPeer;
import org.ethereum.net.client.PeerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.List;

/**
 * www.ethereumJ.com
 *
 * @author: Roman Mandeleil
 * Created on: 27/07/2014 09:12
 */

public class EthereumImpl implements Ethereum {

    private Logger logger = LoggerFactory.getLogger("facade");

    public EthereumImpl() {

    }

    /**
     * Find a peer but not this one
     * @param peerData - peer to exclude
     * @return online peer
     */
    @Override
    public PeerData findPeer(PeerData peerData){

        logger.info("Looking for online peer");
        EthereumListener listener = WorldManager.getInstance().getListener();
        if (listener != null)
            listener.trace("Looking for online peer");


        WorldManager.getInstance().startPeerDiscovery();
        List<PeerData> peers = WorldManager.getInstance().getPeers();
        boolean found = false;
        int i = 0;
        while (!found){

            if (peers.isEmpty()) { sleep10Milli(); continue;}
            if (peers.size()<= i) { i=0; continue;}

            PeerData peer = peers.get(i);
            if (peer.isOnline() && !peer.equals(peerData)){

                logger.info("Found peer: {}", peer.toString());

                if (listener != null)
                    listener.trace(String.format("Found online peer: [ %s ]", peer.toString()));

                return peer;
            }
            ++i;
        }
        return null;
    }

    @Override
    public PeerData findPeer(){
        return findPeer(null);
    }

    @Override
    public void stopPeerDiscover(){
        WorldManager.getInstance().stopPeerDiscover();
    }

    @Override
    public void connect(InetAddress addr, int port){
        connect(addr.getHostName(), port);
    }

    @Override
    public void connect(String ip, int port){

        logger.info("Connecting to: {}:{}", ip, port);
        new ClientPeer().connect(ip,
                port);
    }

    @Override
    public Block getBlockByIndex(long index){
        Block block = WorldManager.getInstance().getBlockchain().getByNumber(index);
        return block;
    }

    @Override
    public long getBlockChainSize(){
        return WorldManager.getInstance().getBlockchain().getSize();
    }

    @Override
    public void addListener(EthereumListener listener) {
        WorldManager.getInstance().addListener(listener);
    }


    private void sleep10Milli(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void loadBlockChain() {
        WorldManager.getInstance().loadBlockchain();
    }
}
