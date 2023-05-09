package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.*;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    private final String baseDir;
    public DocumentPersistenceManager(){
        this.baseDir = System.getProperty("user.dir");
    }
    public DocumentPersistenceManager(File baseDir){
        this.baseDir = baseDir.toString();
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        Gson gson = new Gson();
        JsonSerializer<Document> serializer = new JsonSerializer<Document>() {
            @Override
            public JsonElement serialize(Document document, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject json = new JsonObject();
                json.addProperty("uri", document.getKey().toString());
                if (document.getDocumentTxt() != null) {
                    json.addProperty("documentText", document.getDocumentTxt());
                    json.add("wordMap", gson.toJsonTree(document.getWordMap()));
                }
                else {
                    json.addProperty("byteArray", Base64.getEncoder().encodeToString(document.getDocumentBinaryData()));
                }
                return json;
            }
        };
        JsonElement jsonElement = serializer.serialize(val, null, null);
        String fileName = uri.toString() + ".json";
        //Remove http:// to create the file name
        if (uri.toString().startsWith("http://")) {
            fileName = fileName.substring(7);
        }
        // Create the path of directories to create
        // If there are a series of directories to create, get the
        // file name by only taking from the last / and on
        int lastIndex = fileName.lastIndexOf('/');
        String directoryPathString = "";
        if (lastIndex != -1) {
            directoryPathString = fileName.substring(0, lastIndex);
            fileName = fileName.substring(lastIndex + 1);
        }
        try {
            //Create new directories if need be
            String fullPath = this.baseDir + "/" + directoryPathString;
            Path directoryPath = Paths.get(fullPath);
            try {
                Files.createDirectories(directoryPath);
                if (lastIndex != -1) {
                    System.out.println(fileName + " directory created successfully");
                }
            }
            catch (Exception e) {
                System.out.println("Error creating directory: " + e.getMessage());
                return;
            }
            //Write the new file in the directory
            FileWriter fileWriter = new FileWriter(fullPath + "/" + fileName);
            gson.toJson(jsonElement, fileWriter);
            fileWriter.close();
            System.out.println(fileName + " written successfully to disk");
        }
        catch (IOException e) {
            throw new IOException("Error writing " + fileName + "to disk: " + e.getMessage());
        }
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        String fileName = uri.toString() + ".json";
        if (uri.toString().startsWith("http://")) {
            fileName = fileName.substring(7);
        }
        int lastIndex = fileName.lastIndexOf('/');
        String directoryPathString = "";
        if (lastIndex != -1) {
            directoryPathString = fileName.substring(0, lastIndex);
            fileName = fileName.substring(lastIndex + 1);
        }
        String fullPath = this.baseDir + "/" + directoryPathString;
        //Create a path for the directory of the file
        Path directoryPath = Paths.get(fullPath);
        // Create a Path object for the actual JSON file
        Path filePath = directoryPath.resolve(fileName);
        // create a BufferedReader to read the contents of the file
        BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()));
        // use Gson's JsonParser to parse the file contents into a JsonElement
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(reader);
        // close the reader
        reader.close();
        JsonDeserializer<Document> deserializer = new JsonDeserializer<Document>() {
            @Override
            public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String uriString = jsonObject.get("uri").getAsString();
                URI uri;
                try {
                    uri = new URI(uriString);
                }
                catch (URISyntaxException x) {
                    throw new IllegalArgumentException(x.getMessage(), x);
                }
                try {
                    String documentText = jsonObject.get("documentText").getAsString();
                    JsonObject hashMapJsonObject = jsonObject.getAsJsonObject("wordMap");
                    Map<String, Integer> wordMap = new HashMap<>();
                    for (Map.Entry<String, JsonElement> entry : hashMapJsonObject.entrySet()) {
                        String string = entry.getKey();
                        JsonElement value = entry.getValue();
                        JsonPrimitive primitive = value.getAsJsonPrimitive();
                        Number n = primitive.getAsNumber();
                        Integer integer = n.intValue();
                        wordMap.put(string, integer);
                    }
                    return new DocumentImpl(uri, documentText, wordMap);
                }
                catch (Exception e) {
                    JsonElement byteElement = jsonObject.get("byteArray");
                    byte[] byteArray = Base64.getDecoder().decode(byteElement.getAsString());
                    return new DocumentImpl(uri, byteArray);
                }
            }
        };
        Document d = deserializer.deserialize(json, null, null);
        delete(d.getKey());
        return d;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        String deletedFile = uri.toString() + ".json";
        if (uri.toString().startsWith("http://")) {
            deletedFile = deletedFile.substring(7);
        }
        // Create the path of directories to create
        // If there are a series of directories to create, get the
        // file name by only taking from the last / and on
        int lastIndex = deletedFile.lastIndexOf('/');
        String directoryPathString = this.baseDir;
        if (lastIndex != -1) {
            directoryPathString = this.baseDir + "/" + deletedFile.substring(0, lastIndex);
            deletedFile = deletedFile.substring(lastIndex + 1);
        }
        File file = new File(directoryPathString, deletedFile);
        if (file.delete()) {
            System.out.println(deletedFile + " deleted successfully from disk");
            File directory = new File(directoryPathString);
            deleteDirectory(directory);
            return true;
        }
        else {
            System.out.println("Failed to delete " + deletedFile);
            return false;
        }
    }
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            deleteDSStoreFiles(directory);
            File[] files = directory.listFiles();
            if (files != null && files.length == 0 && !directory.getName().equals("disk")) {
                directory.delete();
                System.out.println("Deleted " + directory + " directory successfully");
                deleteDirectory(directory.getParentFile());
            }
        }
    }
    public void deleteDSStoreFiles(File directory) {
        if (!directory.isDirectory()) {
            return;
        }
        File[] allFiles = directory.listFiles();
        if (allFiles != null && allFiles.length > 0) {
            for (File f: allFiles) {
                if (f.isDirectory()) {
                    deleteDSStoreFiles(f);
                }
                else if (f.getName().equalsIgnoreCase(".DS_Store")) {
                    f.delete();
                }
            }
        }
    }
}
