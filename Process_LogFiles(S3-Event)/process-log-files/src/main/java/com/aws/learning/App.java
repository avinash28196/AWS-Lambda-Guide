package com.aws.learning;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.aws.learning.model.AgentModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Lambda function entry point. You can change to use other pojo type or implement
 * a different RequestHandler.
 *
 * @see <a href=https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html>Lambda Java Handler</a> for more information
 */
public class App implements RequestHandler<S3Event, Object> {
//    private final S3Client s3Client;

//    public App() {
//        // Initialize the SDK client outside of the handler method so that it can be reused for subsequent invocations.
//        // It is initialized when the class is loaded.
//        s3Client = DependencyFactory.s3Client();
//        // Consider invoking a simple api here to pre-warm up the application, eg: dynamodb#listTables
//    }

    @Override
    public Object handleRequest(final S3Event s3Event , final Context context) {
        // TODO: invoking the api call using s3Client.

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        LambdaLogger logger = context.getLogger();

        logger.log("Aloha");
        logger.log("CONTEXT: " + gson.toJson(context));
        logger.log("Input: " + gson.toJson(s3Event));

        logger.log("Thank You");

        System.out.println(System.getenv("USERNAME"));

        AgentModel agentModel = new AgentModel();

        try{
            S3EventNotification.S3EventNotificationRecord record = s3Event.getRecords().get(0);
            String srcBucket = record.getS3().getBucket().getName();
            agentModel.setBucket(srcBucket);

            // Object key may have spaces or unicode non-ASCII characters.
            String srcKey = record.getS3().getObject().getKey().replace('+', ' ');
            String[] folderName = srcKey.split("/");
            agentModel.setFolder(folderName[0]);

            //Find the file type
            String fileType = getFileType(srcKey);
            agentModel.setType(fileType);

            //Read the data
            String data = readFileContent(srcBucket, srcKey);
            agentModel.setData(data);

            logger.log(new Gson().toJson(agentModel));

        }catch (Exception e){
             e.printStackTrace();
        }



        return agentModel;
    }

    /**
     * To Read the content of the file
     * @param srcBucket
     * @param srcKey
     * @return string of data.
     */
    private String readFileContent(String srcBucket, String srcKey) {
        String message = null;
        try{
            AmazonS3 s3Client = new AmazonS3Client();
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(
                    srcBucket, srcKey));
            InputStream objectData = s3Object.getObjectContent();
            message = IOUtils.toString(objectData);
        }catch (Exception e){
            e.printStackTrace();
        }
        return message;
    }

    /**
     * Determine the file type.
     * @param srcKey
     * @return fileType
     * @throws UnsupportedEncodingException
     */
    private String getFileType(String srcKey) throws UnsupportedEncodingException {
        srcKey = URLDecoder.decode(srcKey, "UTF-8");

        String[] folderName = srcKey.split("/");
        String[] arr = folderName[1].split("-");
        String fileType = null;
        if(arr.length > 1){
            fileType = arr[1];
        }
        fileType = srcKey.substring(srcKey.length()-3);

        return fileType;
    }
}

