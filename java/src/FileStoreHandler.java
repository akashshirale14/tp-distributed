import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class FileStoreHandler implements FileStore.Iface {
	/* Constants storing the meta-data information */
	private static final String VERSION = "Version";
	private static final String FILE_NAME = "Filename";
	private static final String CONTENT_HASH = "Content_hash";
	private static final String OWNER = "Owner";

	public int port;
	public String ip;
	public NodeID currentNode;
	private HashMap<String, HashMap<String, String>> nMetadata;
	private List<NodeID> fingerTable;

	public FileStoreHandler(String ip, int port) {
		this.port = port;
		this.ip = ip;
		String key = ip + ":" + port;
		currentNode = new NodeID(getSHA256(key), ip, port);
		nMetadata = new HashMap<String, HashMap<String, String>>();
	}

	
	public void writeFile(RFile rFile) throws SystemException, TException {
		
	}

	
	public RFile readFile(String filename) throws SystemException, TException {
		RFile rfile=null;
		return rfile;
	}

	
	public void setFingertable(List<NodeID> node_list) throws TException {
		fingerTable = node_list;
	}

	
	public NodeID findSucc(String key) throws SystemException, TException {
		NodeID node =null;

		return node;
		
	}

	
	public NodeID findPred(String key) throws SystemException, TException {
		NodeID n1 = null;
		
		return n1;
	}

	/**
	 * Checks if SHA-256 key is in between id1 and id2
	 *
	 * @return true is key belongs to (id1,id2)
	 */
	/*private boolean inBetween(String key, String id1, String id2) {
		if ((key.compareTo(id1) > 0) && (key.compareTo(id2) < 0))
			return true;
		else if ((id1.compareTo(id2) > 0)) {
			if ((key.compareTo(id1) < 0) && (key.compareTo(id2) < 0)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}*/

	
	public NodeID getNodeSucc() throws SystemException, TException {
		NodeID node =null;

		return node;
	}

	/**
	 * @param key
	 *            to be converted
	 * @return SHA-256 equivalent of the key
	 */
	public String getSHA256(String key) {
		StringBuilder result = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(key.getBytes());
			byte[] data = md.digest();
			for (int i = 0; i < data.length; i++) {
				result.append(String.format("%02x", data[i]));
			}
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Exception occured while calculating SHA-256:" + e.getMessage());
			System.exit(0);
		}
		return result.toString();
	}

	/**
	 * This function is used only for findPred method.
	 *
	 * @param node
	 * @param key
	 * @return Server node
	 */
	public NodeID rpcToNode(NodeID node, String key) {
		TTransport transport = null;
		try {
			transport = new TSocket(node.getIp(), node.getPort());
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);
			FileStore.Client client = new FileStore.Client(protocol);
			return client.findPred(key);

		} catch (TTransportException e) {
			System.err.println("Exception occured:" + e.getMessage());
			System.exit(0);
		} catch (SystemException e) {
			System.err.println("Exception occured:" + e.getMessage());
			System.exit(0);
		} catch (TException e) {
			System.err.println("Exception occured:" + e.getMessage());
			System.exit(0);
		}
		return node;
	}

}
