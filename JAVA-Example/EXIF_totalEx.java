package Exif_Libray;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata.ImageMetadataItem;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

public class EXIF_totalEx {
	public static void main(String[] args) {
        //이미지 파일 경로
        String imageFilePath = "C:/Users/user/Desktop/modified_exif7.jpg";
        File file = new File(imageFilePath);
        

        try {
        	//이미지 메타데이터 읽기
            ImageMetadata metadata = Imaging.getMetadata(file);

            if (metadata instanceof JpegImageMetadata) {
                JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                
                //전체 태그 출력 
                printAllExifMetadata(jpegMetadata);
                System.out.println();
                //선택 태그 출력
                printTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_XRESOLUTION);
                printTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_DATE_TIME);
                printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED);
                printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_ISO);
                printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_SHUTTER_SPEED_VALUE);
                printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
                printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_BRIGHTNESS_VALUE);
                printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
                printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LATITUDE);
                printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
                printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LONGITUDE);
                System.out.println();
                //태그값 변경 
                try {
					changeExifMetadata(file);
				} catch (ImageWriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
            }
        } catch (ImageReadException | IOException e) {
            e.printStackTrace();
        }
               //커스텀 태그 추가
    }

    //메타데이터 선택 태그 출력  메소드 
	private static void printTagValue(final JpegImageMetadata jpegMetadata, final TagInfo tagInfo) {
        final TiffField field = jpegMetadata.findEXIFValueWithExactMatch(tagInfo);
        if (field == null) {
            System.out.println(tagInfo.name + ": " + "Not Found.");
        } else {
            System.out.println(tagInfo.name + ": " + field.getValueDescription());
        }
    }
	//메타데이터 전체 태그 출력 메소드
	private static void printAllExifMetadata(JpegImageMetadata jpegMetadata) throws ImageReadException {
        final TiffImageMetadata exifMetadata = jpegMetadata.getExif();
        if (exifMetadata != null) {
            for (final TiffField meta : exifMetadata.getAllFields()) {
			    System.out.println(meta);
			}
        }  
	}
	//메타데이터 태그 수정 메소드
	public static void changeExifMetadata(final File jpegImageFile)
	        throws IOException, ImageReadException, ImageWriteException {
		// 임시 파일 생성
		File newFile = File.createTempFile("temp_", ".jpg");
		
		try (FileOutputStream fos = new FileOutputStream(newFile);
             OutputStream os = new BufferedOutputStream(fos);){
		
	        TiffOutputSet outputSet = null;

	        // note that metadata might be null if no metadata is found.
	         ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
	         JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
	        if (null != jpegMetadata) {
	            // note that exif might be null if no Exif metadata is found.
	            final TiffImageMetadata exif = jpegMetadata.getExif();

	            if (null != exif) {
	                outputSet = exif.getOutputSet();
	            }
	        }
		        if (null == outputSet) {
		            outputSet = new TiffOutputSet();
		        }
		        final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
		        exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED);
		        exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED, "1970:01:02 12:34:56");
		        exifDirectory.removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT);
		        exifDirectory.add(ExifTagConstants.EXIF_TAG_USER_COMMENT, "test");
		        exifDirectory.removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT);
		        
		        
	        new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);
	        Files.move(newFile.toPath(), jpegImageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	        
		}   
	}
	
	  // 메타데이터 전체 태그 출력 메소드(키 :값)
    public static void printAllExifMetadata_Simple(JpegImageMetadata jpegMetadata) {
        List<ImageMetadataItem>  ImageMetadata = jpegMetadata.getItems();
        if (ImageMetadata != null) {
        	for(int i = 0; i<ImageMetadata.size(); i++) {	
        		System.out.println(ImageMetadata.get(i));
            }
        }
    }
	
}
