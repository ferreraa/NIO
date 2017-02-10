package jus.aor.nio.v3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * @author morat 
 */
public class WriteCont extends Continuation{
	private SelectionKey key;
	// state automata
	private enum State{WRITING_DONE, WRITING_LENGTH,WRITING_DATA;}
	// initial state
	protected State state = State.WRITING_DONE;
	// the list of bytes messages to write
	protected ArrayList<byte[]> msgs = new ArrayList<>() ;
	// buf contains the byte array that is currently written
	protected ByteBuffer buf = null;
	protected ByteBuffer lbuf = null;


	/**
	 * @param k
	 * @param sc
	 */
	public WriteCont(SelectionKey k,SocketChannel sc){
		super(sc);
		key = k;
	}


	/**
	 * @return true if the msgs are not completely written.
	 */
	protected boolean isPendingMsg(){
		return state!=State.WRITING_DONE;
	}


	/**
	 * @param data
	 * @throws IOException 
	 */
	protected void sendMsg(Message data) throws IOException{
		msgs.add(data.marshall());
		key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		System.out.println(data.toString());
	}


	/**
	 * @throws IOException
	 */
	protected void handleWrite()throws IOException{
		int remaining;
		switch(state){
		case WRITING_DONE: 
			byte[] msg = msgs.remove(0);
			buf = ByteBuffer.wrap(msg);
			lbuf = Continuation.intToBytes(msg.length);
		case WRITING_LENGTH:
			remaining = socketChannel.write(lbuf);
			if(remaining>0){
				state = State.WRITING_LENGTH;
				break;
			}
		case WRITING_DATA:
			remaining = socketChannel.write(buf);
			if(remaining>0){
				state = State.WRITING_DONE;
				if(msgs.size()==0)
					key.interestOps(SelectionKey.OP_READ);
			}
		}		
	
	}
}
