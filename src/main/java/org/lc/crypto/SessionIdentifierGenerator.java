package org.lc.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class SessionIdentifierGenerator {
	  private static SecureRandom random = new SecureRandom();

	  public static String nextSessionId() {
	    return new BigInteger(130, random).toString(32);
	  }
}
