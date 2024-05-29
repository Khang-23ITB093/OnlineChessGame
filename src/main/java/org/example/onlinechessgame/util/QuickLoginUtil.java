package org.example.onlinechessgame.util;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class QuickLoginUtil {

    public static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    public static final String filepath = "src/main/resources/login.xml";


    public static void saveLoginInfo(String username, String password) throws Exception {
        Path path = Paths.get(filepath);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        SecretKey secretKey = generateKey();
        String encryptedUsername = encrypt(username, secretKey);
        String encryptedPassword = encrypt(password, secretKey);

        // Lưu secretKey dưới dạng Base64
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        // Tạo file XML
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("login");
        document.appendChild(root);

        Element userElement = document.createElement("username");
        userElement.appendChild(document.createTextNode(encryptedUsername));
        root.appendChild(userElement);

        Element passElement = document.createElement("password");
        passElement.appendChild(document.createTextNode(encryptedPassword));
        root.appendChild(passElement);

        Element keyElement = document.createElement("key");
        keyElement.appendChild(document.createTextNode(encodedKey));
        root.appendChild(keyElement);

        // Viết ra file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(Files.newOutputStream(path));
        transformer.transform(domSource, streamResult);
    }

    public static String[] encryptLoginInfo(String username, String password) throws Exception {
        SecretKey secretKey = generateKey();
        String encryptedUsername = encrypt(username, secretKey);
        String encryptedPassword = encrypt(password, secretKey);

        // Lưu secretKey dưới dạng Base64
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        return new String[]{encryptedUsername, encryptedPassword, encodedKey};
    }

    public static String[] readFile() throws Exception {
        // Đọc file XML
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(Files.newInputStream(Paths.get(filepath)));

        String encryptedUsername = document.getElementsByTagName("username").item(0).getTextContent();
        String encryptedPassword = document.getElementsByTagName("password").item(0).getTextContent();
        String encodedKey = document.getElementsByTagName("key").item(0).getTextContent();

        return new String[]{encryptedUsername, encryptedPassword, encodedKey};
    }

    private static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256); // hoặc 128 hoặc 192 bit
        return keyGenerator.generateKey();
    }

    private static String encrypt(String input, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String input, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(input));
        return new String(decryptedBytes);
    }

}
