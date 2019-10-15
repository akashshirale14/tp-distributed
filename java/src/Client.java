import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class Client {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Incorrect number of arguments! Client is terminating...");
			System.exit(0);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		TTransport transport = new TSocket(host, port);
		try {
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			FileStore.Client client = new FileStore.Client(protocol);
			testingServer(client);
		} catch (TException e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Calls the test-cases
	 *
	 * @param client
	 * @throws TException
	 */
	private static void testingServer(FileStore.Client client) throws TException {
		String filename = "example.txt";
		String owner = "guest";
		// Negative Scenario
		System.out.println("Writing the File: " + owner + ":" + filename);
		try {
			testingNegativeWrite(client, filename, owner);
		} catch (SystemException e) {
			System.out.println("SystemException occured during writeFile! " + e.getMessage());
			System.out.println("TestCase 1: Passed");
		}
		System.out.println("\nReading the File: " + owner + " : " + filename);
		try {
			testingNegativeRead(client, filename, owner);
		} catch (SystemException e) {
			System.out.println("SystemException occured during readFile! " + e.getMessage());
			System.out.println("TestCase 2: Passed\n");
		}

		// Positive Scenario
		System.out.println("Writing the File: " + owner + " : " + filename);
		testingWriteFile(client, filename, owner);
		System.out.println("TestCase 3: Passed\n");

		System.out.println("Reading the File: " + owner + " : " + filename);
		testingReadFile(client, filename, owner);
		System.out.println("TestCase 4: Passed\n");

	}

	private static void testingNegativeRead(FileStore.Client client, String filename, String owner)
			throws SystemException {
		System.out.println("TestCase2: Negative case. Read file directly to the mentioned server");
		try {
			RFile rfile = client.readFile(filename, owner);
			System.out.println("File read successfully. Content:");
			System.out.println(rfile.getContent());
			System.out.println("Filename: " + rfile.getMeta().getFilename());
			System.out.println("Onwer: " + rfile.getMeta().getOwner());
			System.out.println("Version: " + rfile.getMeta().getVersion());
		} catch (SystemException e) {
			throw e;
		} catch (TException e) {
			System.err.println("Error occured while executing read request: " + e.getMessage());
		}
	}

	private static void testingNegativeWrite(FileStore.Client client, String filename, String owner)
			throws SystemException {
		System.out.println("TestCase1: Negative case. Write file directly to the mentioned server");
		RFile rFile = new RFile();
		RFileMetadata metadata = new RFileMetadata();
		metadata.setFilename(filename);
		metadata.setFilenameIsSet(true);
		metadata.setOwner(owner);
		metadata.setOwnerIsSet(true);
		String key = metadata.getOwner() + ":" + metadata.getFilename();
		String contentHash = getSHA256(key);
		metadata.setContentHash(contentHash);
		metadata.setContentHashIsSet(true);

		rFile.setMeta(metadata);
		String content = null;
		try {
			byte[] byteContent = Files.readAllBytes(Paths.get("example.txt"));
			content = new String(byteContent);
			rFile.setContent(content);
			rFile.setContentIsSet(true);
			client.writeFile(rFile);

		} catch (IOException e) {
			System.err.println("IO Exception occured: " + e.getMessage());
			System.exit(0);
		} catch (SystemException e) {
			throw e;
		} catch (TException e) {
			System.err.println("Error occured while executing write request: " + e.getMessage());
			System.exit(0);
		}

	}

	/**
	 * Testing the Read Operation on DHT Chord
	 *
	 * @param client
	 *            where the request is instantiated.
	 * @param filename
	 * @param owner
	 */
	private static void testingReadFile(FileStore.Client client, String filename, String owner) {
		System.out.println("TestCase4: Read file directly to the mentioned server");
		String key = owner + ":" + filename;
		String contentHash = getSHA256(key);
		try {
			NodeID destNode = client.findSucc(contentHash);
			TTransport transport = new TSocket(destNode.getIp(), destNode.getPort());
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			FileStore.Client readerClient = new FileStore.Client(protocol);

			RFile rfile = readerClient.readFile(filename, owner);
			System.out.println("File read successfully. Content:");
			System.out.println(rfile.getContent());
			System.out.println("Filename: " + rfile.getMeta().getFilename());
			System.out.println("Onwer: " + rfile.getMeta().getOwner());
			System.out.println("Version: " + rfile.getMeta().getVersion());

		} catch (SystemException e) {
			System.err.println("SystemException occured: " + e.getMessage());
			// System.exit(0);
		} catch (TException e) {
			System.err.println("Error occured while executing read request: " + e.getMessage());
			System.exit(0);
		}

	}

	/**
	 * Testing the Write Operation on DHT Chord
	 *
	 * @param client
	 *            where the request is instantiated.
	 * @param filename
	 * @param owner
	 */
	private static void testingWriteFile(FileStore.Client client, String filename, String owner) {
		System.out.println("TestCase4: Read file directly to the mentioned server");
		RFile rFile = new RFile();
		RFileMetadata metadata = new RFileMetadata();
		metadata.setFilename(filename);
		metadata.setFilenameIsSet(true);
		metadata.setOwner(owner);
		metadata.setOwnerIsSet(true);
		String key = metadata.getOwner() + ":" + metadata.getFilename();
		String contentHash = getSHA256(key);
		metadata.setContentHash(contentHash);
		metadata.setContentHashIsSet(true);

		rFile.setMeta(metadata);
		String content = null;
		try {
			byte[] byteContent = Files.readAllBytes(Paths.get("example.txt"));
			content = new String(byteContent);
			rFile.setContent(content);
			rFile.setContentIsSet(true);

			NodeID destNode = client.findSucc(contentHash);
			TTransport transport = new TSocket(destNode.getIp(), destNode.getPort());
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			FileStore.Client writerClient = new FileStore.Client(protocol);

			writerClient.writeFile(rFile);

		} catch (IOException e) {
			System.err.println("IO Exception occured: " + e.getMessage());
			System.exit(0);
		} catch (SystemException e) {
			System.err.println("SystemException occured during writeFile!" + e.getMessage());
			System.exit(0);
		} catch (TException e) {
			System.err.println("Error occured while executing write request: " + e.getMessage());
			System.exit(0);
		}

	}

	/**
	 * @param key
	 *            to be converted
	 * @return SHA-256 equivalent of the key
	 */
	public static String getSHA256(String key) {
		StringBuilder result = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(key.getBytes());
			byte[] data = md.digest();
			for (int i = 0; i < data.length; i++) {
				result.append(String.format("%02x", data[i]));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

}
