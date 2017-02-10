package jus.aor.nio.v3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;


/**
 * @author morat 
 */
public class ReadCont  extends Continuation{

	private enum State{READING_DONE, READING_LENGTH,READING_DATA;}
	// initial state
	protected State state = State.READING_DONE;
	// the list of bytes messages to read
	protected ArrayList<byte[]> msgs = new ArrayList<>() ;
	// buf contains the byte array that is currently read
	protected ByteBuffer buf = null;
	
	protected ByteBuffer buf_length = null;
	//taille du message indiquant la longueur du message à venir
	protected int LENGTHSIZE = 4;
	
	protected int nbsteps;
	//nombre de lectures necessaires pour lire completement un message.

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
		Message m = null; //message qui sera retourné par la méthode
		
		switch(state)
		{
		case READING_DONE:
			//On lit un nouveau message
			buf_length = ByteBuffer.allocate(LENGTHSIZE);
			state = State.READING_LENGTH;
			nbsteps = 1;
					
		case READING_LENGTH:
			//On est en train de lire l'entête
			if(socketChannel.read(buf_length) == -1)
			{
				socketChannel.close();
				return null;				
			}
						
			if(buf_length.remaining()==0)
			{
				state = State.READING_DATA;
				buf = ByteBuffer.allocate(super.bytesToInt(buf_length));
			}
			else
			{
				nbsteps++;
				break; 
				//On sort du switch uniquement si on n'a pas fini de lire la taille.
			}
		case READING_DATA:
			//On est en train de lire le contenu du message
			if(socketChannel.read(buf) == -1)
			{
				socketChannel.close();
				return null;				
			}
			
			if(buf.remaining()==0)
			{
				state = State.READING_DONE;
				m = new Message(buf.array(),nbsteps);
			}
			else
				nbsteps++;
			
			break;
		}
		if(m!=null) System.out.println(m.toString());
		return m;
		//On retourne le message si il a été créé et donc null dans le cas contraire
	}
}

/**
 * Handle incoming data event
 * @param the key of the channel on which the incoming data waits to be received 
 * @throws IOException 
 */
