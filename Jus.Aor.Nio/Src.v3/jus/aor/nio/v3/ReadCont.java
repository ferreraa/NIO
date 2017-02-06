package jus.aor.nio.v3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author morat 
 */
public class ReadCont  extends Continuation{

	// to complete

	/**
	 * @param sc
	 */
	public ReadCont(SocketChannel sc){
		super(sc);
	}
	/**
	 * @return the message
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	protected Message handleRead() throws IOException, ClassNotFoundException{
	// todo
	}
}

