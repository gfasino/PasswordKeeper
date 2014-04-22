package fasino.passwordkeeper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {

	   public static String encryptString(String str, String key)
	   {
	      StringBuffer sb = new StringBuffer (str);

	      int lenStr = str.length();
	      int lenKey = key.length();
		   
	      //
	      // For each character in our string, encrypt it...
	      for ( int i = 0, j = 0; i < lenStr; i++, j++ ) 
	      {
	         if ( j >= lenKey ) j = 0;  // Wrap 'round to beginning of key string.

	         //
	         // XOR the chars together. Must cast back to char to avoid compile error. 
	         //
	         sb.setCharAt(i, (char)(str.charAt(i) ^ key.charAt(j))); 
	      }

	      return sb.toString();
	   }
	   
		public static String decryptString(String str, String key)
		{
			// To 'decrypt' the string, simply apply the same technique.
			return encryptString(str, key);
		}
		
		private static String sep = "x";
		
		public static String encrypt(String data, String key)
		{
			String encrypted = "";
			int j=0;
			for(int i=0;i<data.length();i++)
			{
				int vData = (int) data.charAt(i);
				int vPass = (int) key.charAt(j);
				int toAppend = vData*vPass+vPass;
				encrypted += Integer.toString(toAppend) + sep; 
				j++;
				if(j >= key.length())
					j=0;
			}
			return encrypted;
		}
		
		public static String decrypt(String data, String key)
		{
			String[] decrypted = data.split(sep);
			String decrypter = "";
			int j=0;
			for(int i=0;i<decrypted.length;i++)
			{
				int value = Integer.parseInt(decrypted[i]);
				int vPass = (int) key.charAt(j);
				int toAppend = (value-vPass)/vPass;
				
				j++;
				if(j >= key.length())
					j=0;
				decrypter += (char) toAppend;
			}
			return decrypter;
		}
		
		public static String md5(String s) {
		    try {
		        // Create MD5 Hash
		        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
		        digest.update(s.getBytes());
		        byte messageDigest[] = digest.digest();

		        // Create Hex String
		        StringBuffer hexString = new StringBuffer();
		        for (int i=0; i<messageDigest.length; i++)
		            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
		        return hexString.toString();

		    } catch (NoSuchAlgorithmException e) {
		        e.printStackTrace();
		    }
		    return "";
		}
}
