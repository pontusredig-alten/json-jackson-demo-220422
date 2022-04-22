package se.iths;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class JacksonDemo {

    // User för demo
    User userObject = new User("Kalle", "Anka", "kalle@ankeborg.se", "0700123456",
            new Address("Paradisäppelvägen 111", "12345", "Ankeborg"));

    // File att generera / läsa ifrån
    final File JSON_FILE = new File("user.json");

    // Återanvänd ObjectMapper i alla metoder - kostsam att instansiera
    final ObjectMapper mapper = new ObjectMapper();


    public static void main(String[] args) throws IOException {
        JacksonDemo demo = new JacksonDemo();

//        demo.javaObjectToJsonFile();
//        demo.javaObjectToJsonOutput();
//        demo.javaObjectToJsonString();
//        demo.jsonFileToJavaObject();
//        demo.javaObjectToJsonWithViews();
//        demo.jsonFileToJsonNode();
//        demo.parseJsonFileThroughStreaming();
        demo.jsonGenerator();

    }

    public void javaObjectToJsonFile() throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(JSON_FILE, userObject);
    }

    public void javaObjectToJsonOutput() throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, userObject);
    }

    public void javaObjectToJsonString() throws IOException {
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userObject);
        System.out.println("JSON STRING: " + jsonString);
    }

    public void jsonFileToJavaObject() throws IOException {
        User userFromJsonFile = mapper.readValue(JSON_FILE, User.class);
        System.out.println("JAVA OBJECT PARSED FROM FILE: " + userFromJsonFile);
    }

    public void javaObjectToJsonWithViews() throws IOException {
        // Format JSON
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String jsonString =
                mapper
                        .writerWithView(JsonViews.Admin.class)
                        .writeValueAsString(userObject);

        System.out.println("JSON STRING WITH VIEWS: " + jsonString);
    }

    public void jsonFileToJsonNode() throws IOException {

        JsonNode rootNode = mapper.readTree(JSON_FILE);

        Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode childNode = field.getValue();
            System.out.println("KEY: " + fieldName);
            System.out.println("VALUE: " + childNode);
        }

        // Hämta värde från RootNode
        String phoneNumber = rootNode.get("phone").asText();
        System.out.println("PHONE NUMBER: " + phoneNumber);

        // Hämta värde från SubNode
        JsonNode addressNode = rootNode.path("address");
        String city = addressNode.get("city").asText();
        System.out.println("CITY: " + city);


    }

    public void parseJsonFileThroughStreaming() throws IOException {
        JsonFactory factory = new JsonFactory();

        try (JsonParser parser = factory.createParser(JSON_FILE)) {

            while (parser.nextToken() != null) {

//                System.out.println("TOKEN NAME: " + parser.currentToken());
//                System.out.println("FRÅN PARSER: " + parser.getText());
//

                if (parser.currentToken().isScalarValue()) {
                    String currentName = parser.currentName();
                    if (currentName != null) {
                        String text = parser.getText();
                        System.out.println(currentName + " : " + text);
                    }
                }
            }
        }

    }

    public void jsonGenerator() throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(System.out);
        jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());

        // {
        jsonGenerator.writeStartObject();

        // "förnamn": "Kalle"
        jsonGenerator.writeStringField("förnamn", userObject.getFirstName());
        // "efternamn": "Anka"
        jsonGenerator.writeStringField("efternamn", userObject.getLastName());

        // }
        jsonGenerator.writeEndObject();

        // Skriv ut
        jsonGenerator.flush();

        // Stäng ner
        jsonGenerator.close();

    }

}
