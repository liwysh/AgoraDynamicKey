//package media;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by hefeng on 15/8/10.
 * Util to generate Agora media dynamic key.
 */
public class DynamicKey{
    /**
     * Manipulate Agora dynamic key for media connection.
     * @param vendorKey Vendor key assigned by Agora when register
     * @param signKey Sign key assigned by Agora
     * @param channelName name of channel to join
     * @param unixTs unix timestamp by seconds
     * @param randomInt random uint32 salt for generating dynamic key
     * @return String representation of dynamic key to join Agora media server
     * @throws Exception if any error occurs
     */
    public static String generateDynamicKey(String vendorKey, String signKey, String channelName, int unixTs, int randomInt) throws Exception{
        String unixTsStr = ("0000000000"+Integer.toString(unixTs)).substring(Integer.toString(unixTs).length());
        String randomIntStr = ("00000000"+Integer.toHexString(randomInt)).substring(Integer.toHexString(randomInt).length());
        String signature = generateSignature(vendorKey, signKey, channelName, unixTsStr, randomIntStr);
        return String.format("%s%s%s%s", signature, vendorKey, unixTsStr, randomIntStr);
    }
    private static String generateSignature(String vendorKey, String signKey, String channelName, String unixTsStr, String randomIntStr) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(vendorKey.getBytes());
        baos.write(unixTsStr.getBytes());
        baos.write(randomIntStr.getBytes());
        baos.write(channelName.getBytes());
        byte[] sign = encodeHMAC(signKey, baos.toByteArray());
        return bytesToHex(sign);
    }
    private static byte[] encodeHMAC(String key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec keySpec = new SecretKeySpec(
                key.getBytes(),
                "RAW");

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);
        return mac.doFinal(message);
    }

    private static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    //public static void main(String[] args) {
        //String vkey = "970ca35de60c44645bbae84215061b33b";
        //String signKey = "5cfd2fd1755d40ecb72977518be15d3b";                                                                                                                
        //String cName = "7d72365eb983485397e3e3f9d460bdda";
        //int ts = 1446455472;
        //int rint = 58964981;
        //int uid=999;
        //int expiredTs = 1446455471;
        //int a = (int) (System.currentTimeMillis()/1000);
        //System.out.println("xxx "+ a);
        //try {
            //String res = DynamicKey.generateDynamicKey(vkey, signKey, cName, ts, rint);
            //System.out.println("Dyanmic key to join channel is:"+res);
            //res = DynamicKey.generateDynamicKey2(vkey, signKey, cName, ts, rint, uid, expiredTs);
            //System.out.println("Dyanmic key2 to join channel is:"+res);
            //res = DynamicKey.generateDynamicKey3(vkey, signKey, cName, ts, rint, uid, expiredTs);
            //System.out.println("Dyanmic key3 to join channel is:"+res);
        //}catch (Exception  e){
            //System.err.println("Failed to generate dynamic key");
            //e.printStackTrace();
        //}
    //}
}
