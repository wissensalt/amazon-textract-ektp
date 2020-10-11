package com.wissensalt.rnd;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.AnalyzeDocumentRequest;
import com.amazonaws.services.textract.model.AnalyzeDocumentResult;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.BoundingBox;
import com.amazonaws.services.textract.model.Document;
import com.amazonaws.services.textract.model.S3Object;
import com.amazonaws.services.textract.model.Point;
import com.amazonaws.services.textract.model.Relationship;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;

/**
 * @author : <a href="mailto:wissensalt@gmail.com">Achmad Fauzi</a>
 * @since : 2020-10-11
 **/
public class AnalyzeDocument extends JPanel {

    private static final long serialVersionUID = 1L;

    BufferedImage image;

    AnalyzeDocumentResult result;

    public AnalyzeDocument(AnalyzeDocumentResult documentResult, BufferedImage bufImage) throws Exception {
        super();


        result = documentResult; // Results of text detection.
        image = bufImage; // The image containing the document.

    }

    // Draws the image and text bounding box.
    public void paintComponent(Graphics g) {

        int height = image.getHeight(this);
        int width = image.getWidth(this);

        Graphics2D g2d = (Graphics2D) g; // Create a Java2D version of g.

        // Draw the image.
        g2d.drawImage(image, 0, 0, image.getWidth(this), image.getHeight(this), this);

        // Iterate through blocks and display bounding boxes around everything.

        IndonesiaCitizenIdExtractor extractor = new IndonesiaCitizenIdExtractor();
        List<Block> blocks = result.getBlocks();
        extractor.retrieve("", "", result.getBlocks());
        for (Block block : blocks) {

//            DisplayBlockInfo(block);
            switch(block.getBlockType()) {

                case "KEY_VALUE_SET":
                    if (block.getEntityTypes().contains("KEY")){
                        ShowBoundingBox(height, width, block.getGeometry().getBoundingBox(), g2d, new Color(255,0,0));
                    }
                    else {  //VALUE
                        ShowBoundingBox(height, width, block.getGeometry().getBoundingBox(), g2d, new Color(0,255,0));
                    }
                    break;
                case "TABLE":
                    ShowBoundingBox(height, width, block.getGeometry().getBoundingBox(), g2d, new Color(0,0,255));
                    break;
                case "CELL":
                    ShowBoundingBox(height, width, block.getGeometry().getBoundingBox(), g2d, new Color(255,255,0));
                    break;
                default:
                    //PAGE, LINE & WORD
                    //ShowBoundingBox(height, width, block.getGeometry().getBoundingBox(), g2d, new Color(200,200,0));
            }

            ShowPolygon(height,width, block.getGeometry().getPolygon(),g2d);
        }

        // uncomment to show polygon around all blocks
//        ShowPolygon(height,width, block.getGeometry().getPolygon(),g2d);


    }

    // Show bounding box at supplied location.
    private void ShowBoundingBox(int imageHeight, int imageWidth, BoundingBox box, Graphics2D g2d, Color color) {

        float left = imageWidth * box.getLeft();
        float top = imageHeight * box.getTop();

        // Display bounding box.
        g2d.setColor(color);
        g2d.drawRect(Math.round(left), Math.round(top),
                Math.round(imageWidth * box.getWidth()), Math.round(imageHeight * box.getHeight()));

    }

    // Shows polygon at supplied location
    private void ShowPolygon(int imageHeight, int imageWidth, List<Point> points, Graphics2D g2d) {

        g2d.setColor(new Color(0, 0, 0));
        Polygon polygon = new Polygon();

        // Construct polygon and display
        for (Point point : points) {
            polygon.addPoint((Math.round(point.getX() * imageWidth)),
                    Math.round(point.getY() * imageHeight));
        }
        g2d.drawPolygon(polygon);
    }
    //Displays information from a block returned by text detection and text analysis
    private void DisplayBlockInfo(Block block) {
        System.out.println("Block Id : " + block.getId());
        if (block.getText()!=null)
            System.out.println("    Detected text: " + block.getText());
        System.out.println("    Type: " + block.getBlockType());

        if (block.getBlockType().equals("PAGE") !=true) {
            System.out.println("    Confidence: " + block.getConfidence().toString());
        }
        if(block.getBlockType().equals("CELL"))
        {
            System.out.println("    Cell information:");
            System.out.println("        Column: " + block.getColumnIndex());
            System.out.println("        Row: " + block.getRowIndex());
            System.out.println("        Column span: " + block.getColumnSpan());
            System.out.println("        Row span: " + block.getRowSpan());

        }

        System.out.println("    Relationships");
        List<Relationship> relationships=block.getRelationships();
        if(relationships!=null) {
            for (Relationship relationship : relationships) {
                System.out.println("        Type: " + relationship.getType());
                System.out.println("        IDs: " + relationship.getIds().toString());
            }
        } else {
            System.out.println("        No related Blocks");
        }

        System.out.println("    Geometry");
        System.out.println("        Bounding Box: " + block.getGeometry().getBoundingBox().toString());
        System.out.println("        Polygon: " + block.getGeometry().getPolygon().toString());

        List<String> entityTypes = block.getEntityTypes();

        System.out.println("    Entity Types");
        if(entityTypes!=null) {
            for (String entityType : entityTypes) {
                System.out.println("        Entity Type: " + entityType);
            }
        } else {
            System.out.println("        No entity type");
        }
        if(block.getPage()!=null)
            System.out.println("    Page: " + block.getPage());
        System.out.println();
    }

    public static void main(String arg[]) throws Exception {

        String accessKey = "";
        String secretKey = "";
        AWSCredentials credentials = new BasicAWSCredentials(
                accessKey, secretKey;
        );

        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(credentials);

        // The S3 bucket and document
        String document = "contoh-ektp.jpeg";
        String bucket = "tripoin-pos-bucket-1";

        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withEndpointConfiguration(
                        new EndpointConfiguration("https://s3.amazonaws.com",Regions.AP_SOUTHEAST_1.getName()))
                .build();


        // Get the document from S3
        com.amazonaws.services.s3.model.S3Object s3object = s3client.getObject(bucket, document);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        BufferedImage image = ImageIO.read(inputStream);

        awsCredentialsProvider.refresh();

        // Call AnalyzeDocument
        EndpointConfiguration endpoint = new EndpointConfiguration(
                "https://textract.ap-southeast-1.amazonaws.com", Regions.AP_SOUTHEAST_1.getName());

        AmazonTextract client = AmazonTextractClientBuilder
                .standard()
                .withCredentials(awsCredentialsProvider)
                .withEndpointConfiguration(endpoint)
                .build();


        AnalyzeDocumentRequest request = new AnalyzeDocumentRequest()
                .withFeatureTypes("TABLES","FORMS")
                .withDocument(new Document().
                        withS3Object(new S3Object().withName(document).withBucket(bucket)));


        AnalyzeDocumentResult result = client.analyzeDocument(request);

        // Create frame and panel.
        JFrame frame = new JFrame("RotateImage");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AnalyzeDocument panel = new AnalyzeDocument(result, image);
        panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);

    }
}