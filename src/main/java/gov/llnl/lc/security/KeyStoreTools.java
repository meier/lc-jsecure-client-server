/************************************************************
 * Copyright (c) 2015, Lawrence Livermore National Security, LLC.
 * Produced at the Lawrence Livermore National Laboratory.
 * Written by Timothy Meier, meier3@llnl.gov, All rights reserved.
 * LLNL-CODE-673346
 *
 * This file is part of the OpenSM Monitoring Service (OMS) package.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (as published by
 * the Free Software Foundation) version 2.1 dated February 1999.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * OUR NOTICE AND TERMS AND CONDITIONS OF THE GNU GENERAL PUBLIC LICENSE
 *
 * Our Preamble Notice
 *
 * A. This notice is required to be provided under our contract with the U.S.
 * Department of Energy (DOE). This work was produced at the Lawrence Livermore
 * National Laboratory under Contract No.  DE-AC52-07NA27344 with the DOE.
 *
 * B. Neither the United States Government nor Lawrence Livermore National
 * Security, LLC nor any of their employees, makes any warranty, express or
 * implied, or assumes any liability or responsibility for the accuracy,
 * completeness, or usefulness of any information, apparatus, product, or
 * process disclosed, or represents that its use would not infringe privately-
 * owned rights.
 *
 * C. Also, reference herein to any specific commercial products, process, or
 * services by trade name, trademark, manufacturer or otherwise does not
 * necessarily constitute or imply its endorsement, recommendation, or favoring
 * by the United States Government or Lawrence Livermore National Security,
 * LLC. The views and opinions of authors expressed herein do not necessarily
 * state or reflect those of the United States Government or Lawrence Livermore
 * National Security, LLC, and shall not be used for advertising or product
 * endorsement purposes.
 *
 *        file: KeyStoreTools.java
 *
 *  Created on: Jun 20, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.security;

import gov.llnl.lc.logging.CommonLogger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**********************************************************************
 * KeyStoreTools contains a random collection of utilities for doing
 * simple or common functions related to keystores.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 20, 2011 8:15:34 AM
 **********************************************************************/
public class KeyStoreTools implements AuthenticationConstants, CommonLogger
{
  private static SimpleTrustManager stm = null;

  private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

  /** logger for the class **/
  private static final java.util.logging.Logger classLogger =
      java.util.logging.Logger.getLogger( "gov.llnl.lc.security.KeyStoreTools" );

  private static String toHexString(byte[] bytes)
  {
    StringBuilder sb = new StringBuilder(bytes.length * 3);
    for (int b : bytes)
    {
      b &= 0xff;
      sb.append(HEXDIGITS[b >> 4]);
      sb.append(HEXDIGITS[b & 15]);
      sb.append(' ');
    }
    return sb.toString();
  }
  
 public static KeyStore getJKS_KeyStore(boolean isClient, String propertyFileName) throws Exception
  {
    KeyStore ks = KeyStore.getInstance("JKS");
    ks.load(new FileInputStream(getKeyStorePath(isClient, propertyFileName)), getKeyStorePasscode(isClient, propertyFileName));
    return ks;
  }
  
  public static String getKeyStorePath(boolean isClient, String propertyFileName)
  {
    String path = null;
    if(isClient)
    {
      // get the path from a properties file, or use the default
      String defaultClientPath = System.getProperty("user.home") + KS_CLIENT_DEFAULT_FILENAME;
      path = KeyStoreTools.getKeyStoreProperties(propertyFileName).getProperty(KS_CLIENT_FILENAME_KEY, defaultClientPath);
      classLogger.info("The Client keystore path is: " + path);
    }
    else
    {
      // get the path from a properties file, or use the default
      String defaultServerPath = System.getProperty("java.home") + KS_SERVER_DEFAULT_FILENAME;
      path = KeyStoreTools.getKeyStoreProperties(propertyFileName).getProperty(KS_SERVER_FILENAME_KEY, defaultServerPath);      
      classLogger.info("The Server keystore path is: " + path);
    }
    return path;
  }

  public static char [] getKeyStorePasscode(boolean isClient, String propertyFileName)
  {
    String key = isClient? KS_CLIENT_PASSCODE_KEY: KS_SERVER_PASSCODE_KEY;
    // get the passcode from a properties file, or use the default
    String passcode = KeyStoreTools.getKeyStoreProperties(propertyFileName).getProperty(key, KS_DEFAULT_PASSCODE);
    classLogger.info("Keystore passcode is: " + passcode!= null ? "valid": "null");
    return passcode.toCharArray();
  }
  
  public static Properties getKeyStoreProperties(String propertyFileName)
  {
    return new KeyStoreProperties(propertyFileName);
  }
  
  public static void installCerts(KeyStore ks1, String hostname, String propertyFileName)throws Exception
  {
    // normally, only clients would programatically install remote certs from servers
    String KeyStorePath = KeyStoreTools.getKeyStorePath(true, propertyFileName);
    char[] KeyStorePasscode = KeyStoreTools.getKeyStorePasscode(true, propertyFileName);
    
    X509Certificate[] chain = stm.chain;
    if(stm == null)
    {
      logger.warning("Could not obtain Simple Trust Manager");
      classLogger.warning("Could not obtain Simple Trust Manager");
      return;      
    }
    if (chain == null)
    {
      logger.warning("Could not obtain server certificate chain");
      classLogger.warning("Could not obtain server certificate chain");
      return;
    }
    KeyStore ks = KeyStore.getInstance("JKS");
    ks.load(new FileInputStream(KeyStorePath), KeyStorePasscode);

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    System.out.println("Host (" + hostname + ") does not recognize Service");
    System.out.println();
    System.out.println("Server sent " + chain.length + " certificate(s):");
    System.out.println();
    MessageDigest sha1 = MessageDigest.getInstance("SHA1");
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    for (int i = 0; i < chain.length; i++)
    {
      X509Certificate cert = chain[i];
      System.out.println(" " + (i + 1) + " Subject " + cert.getSubjectDN());
      System.out.println("   Issuer  " + cert.getIssuerDN());
      sha1.update(cert.getEncoded());
      System.out.println("   sha1    " + toHexString(sha1.digest()));
      md5.update(cert.getEncoded());
      System.out.println("   md5     " + toHexString(md5.digest()));
      System.out.println();
    }

    System.out.println("Enter certificate to add to the trusted keystore or 'q' to quit: [1]");
    String line = reader.readLine().trim();
    int k;
    try
    {
      k = (line.length() == 0) ? 0 : Integer.parseInt(line) - 1;
    }
    catch (NumberFormatException e)
    {
      logger.warning("KeyStore not changed");
      classLogger.warning("KeyStore not changed");
      return;
    }

    X509Certificate cert = chain[k];
    String alias = hostname + "-" + (k + 1);
    ks.setCertificateEntry(alias, cert);

    OutputStream out = new FileOutputStream(KeyStorePath);
    ks.store(out, KeyStorePasscode);
    out.close();

    classLogger.info(cert.toString());
    classLogger.info("Added certificate to keystore" + KeyStorePath + " using alias '" + alias + "'");
    System.out.println("Added certificate to keystore" + KeyStorePath + " using alias '" + alias + "'");
  }
  
  public static KeyManager [] getKeyManagers(boolean isClient, KeyStore ks, String propertyFileName) throws Exception
  {
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(ks, getKeyStorePasscode(isClient, propertyFileName));
    return kmf.getKeyManagers();    
  }
  
  public static TrustManager [] getTrustManagers(KeyStore ks) throws Exception
  {
    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(ks);
    logger.fine("There are " + tmf.getTrustManagers().length + " trust managers");
    X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
    stm = new SimpleTrustManager(defaultTrustManager);
    return new TrustManager[] { stm };    
  }
  
  private static class SimpleTrustManager implements X509TrustManager
  {
    private final X509TrustManager tm;
    private X509Certificate[]      chain;

    SimpleTrustManager(X509TrustManager tm)
    {
      this.tm = tm;
    }

    public X509Certificate[] getAcceptedIssuers()
    {
      throw new UnsupportedOperationException();
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException
    {
      throw new UnsupportedOperationException();
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException
    {
      this.chain = chain;
      tm.checkServerTrusted(chain, authType);
    }
  }
}
