import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Detection {

    public static BufferedImage crop(BufferedImage img) throws IOException, NoninvertibleTransformException {
        final Point2D.Double[] eyes = findEyes(img);

        final Point2D.Double center = new Point2D.Double(img.getWidth() / 2., img.getHeight() / 2.), actCenter = new Point2D.Double((eyes[0].getX() + eyes[1].getX()) / 2, (eyes[0].getY() + eyes[1].getY()) / 2), actRDelta = new Point2D.Double(eyes[1].getX() - actCenter.getX(), eyes[1].getY() - actCenter.getY());

        final double rDelta = img.getWidth() / 16.;

        AffineTransform v = new AffineTransform(actRDelta.getX() / rDelta, actRDelta.getY() / rDelta, -actRDelta.getY() / rDelta, actRDelta.getX() / rDelta, actCenter.getX(), actCenter.getY());
        v.translate(-center.getX(), -center.getY());
        v.invert();

        AffineTransformOp op = new AffineTransformOp(v, AffineTransformOp.TYPE_BICUBIC);

        BufferedImage cropped = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        op.filter(img, cropped);
        return cropped;
    }

    private static Point2D.Double[] findEyes(BufferedImage bImg) throws IOException {
        Feature feature = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(bImg, "PNG", stream);
        Image image = Image.newBuilder().setContent(ByteString.copyFrom(stream.toByteArray())).build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();

        List<AnnotateImageRequest> requests = new ArrayList<>();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            List<AnnotateImageResponse> responses = client.batchAnnotateImages(requests).getResponsesList();
            FaceAnnotation annotation = responses.get(0).getFaceAnnotationsList().get(0);

            return new Point2D.Double[]{new Point2D.Double(annotation.getLandmarks(0).getPosition().getX(), annotation.getLandmarks(0).getPosition().getY()), new Point2D.Double(annotation.getLandmarks(1).getPosition().getX(), annotation.getLandmarks(1).getPosition().getY())};
        }
    }
}