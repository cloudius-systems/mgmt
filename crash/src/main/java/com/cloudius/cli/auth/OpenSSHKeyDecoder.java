package com.cloudius.cli.auth;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class OpenSSHKeyDecoder {
  private byte[] buffer;
  private int pos;

  public PublicKey decodePublicKey(String keyLine) throws Exception {
    buffer = null;
    pos = 0;

    for (String part : keyLine.split(" ")) {
      if (part.startsWith("AAAA")) {
        buffer = DatatypeConverter.parseBase64Binary(part);
        break;
      }
    }
    if (buffer == null) {
      throw new IllegalArgumentException("no Base64 part to decode");
    }

    String type = decodeType();
    switch (type) {
      case "ssh-rsa": {
        BigInteger e = decodeBigInt();
        BigInteger m = decodeBigInt();
        RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
      }
      case "ssh-dss": {
        BigInteger p = decodeBigInt();
        BigInteger q = decodeBigInt();
        BigInteger g = decodeBigInt();
        BigInteger y = decodeBigInt();
        DSAPublicKeySpec spec = new DSAPublicKeySpec(y, p, q, g);
        return KeyFactory.getInstance("DSA").generatePublic(spec);
      }
      default:
        throw new IllegalArgumentException("unknown type " + type);
    }
  }

  private String decodeType() {
    int len = decodeInt();
    String type = new String(buffer, pos, len);
    pos += len;
    return type;
  }

  private int decodeInt() {
    return ((buffer[pos++] & 0xFF) << 24) | ((buffer[pos++] & 0xFF) << 16)
        | ((buffer[pos++] & 0xFF) << 8) | (buffer[pos++] & 0xFF);
  }

  private BigInteger decodeBigInt() {
    int len = decodeInt();
    byte[] bigIntBytes = new byte[len];
    System.arraycopy(buffer, pos, bigIntBytes, 0, len);
    pos += len;
    return new BigInteger(bigIntBytes);
  }
}
