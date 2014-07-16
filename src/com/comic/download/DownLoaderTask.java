package com.comic.download;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.os.AsyncTask;
import android.util.Log;


 public class DownLoaderTask extends AsyncTask<Void, Integer, Long> {
 private final String TAG = "DownLoaderTask";
 private URL mUrl;
 private File mFile;
 private int mProgress = 0;
 public int progress=0;
 public double length=0;
 private ProgressReportingOutputStream mOutputStream;
 
 
 private File mInput;
 private File mOutput;
 private boolean mReplaceAll;
 
 public String zipFile;
 public String targetDir;
 public String fName;
 
 
 public DownLoaderTask(String url,String out){
  super();
  try {
   mUrl = new URL(url);
   String fileName = new File(mUrl.getFile()).getName();
   mFile = new File(out, fileName);
   Log.d(TAG, "out="+out+", name="+fileName+",mUrl.getFile()="+mUrl.getFile());
   
   fName=url.substring(url.lastIndexOf("/") + 1);
   String in=com.cartoon.util.Utils.getSdCardPath()+"/cartoonReader/books/"+fName;
   zipFile=in.trim();
   mInput = new File(in);
   targetDir=out+com.cartoon.util.Utils.getFileNameNoEx(mInput.getName())+"/";
   System.out.println(targetDir);
   mOutput = new File(targetDir);
   if(!mOutput.exists()){
    if(!mOutput.mkdirs()){
     Log.e(TAG, "Failed to make directories:"+mOutput.getAbsolutePath());
       }
     }
      mReplaceAll = true;
  } catch (MalformedURLException e) {
   e.printStackTrace();
    }  
 }
 
 @Override
 protected Long doInBackground(Void... params) {
    download(); 
  if(mFile.exists()){
  try{
	  Long j=unzip();
	  if(j>0){
		  mFile.delete();
		  return j;
	  }
   }catch(Exception e){
	  mFile.delete();
	  if(mOutput.exists()){
		  mOutput.delete();
	  }
    }
   }
  return 1l; 
 }
 
 @Override
 protected void onPostExecute(Long result) {
  if(isCancelled())
	 if(mFile.exists()){
		  mFile.delete();
	  }
   return;
 } 
 
 private long download(){
  URLConnection connection = null;
  int bytesCopied = 0;
  try {
   connection = mUrl.openConnection();
   connection.setRequestProperty("Accept-Encoding", "identity");
   
   length = connection.getContentLength();
   if(mFile.exists()&&length == mFile.length()){
    Log.d(TAG, "file "+mFile.getName()+" already exits!!");
    return 1;
   }
   mOutputStream = new ProgressReportingOutputStream(mFile);
   //publishProgress(0,length);
   bytesCopied =copy(connection.getInputStream(),mOutputStream);
   if(bytesCopied!=length&&length!=-1){
    Log.e(TAG, "Download incomplete bytesCopied="+bytesCopied+", length"+length);
   }
   mOutputStream.close();
  } catch (IOException e) {
   e.printStackTrace();
  }
  return bytesCopied;
 }
 
 private int copy(InputStream input, OutputStream output){
  byte[] buffer = new byte[1024*8];
  BufferedInputStream in = new BufferedInputStream(input, 1024*8);
  BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
  int count =0,n=0;
  try {
   while((n=in.read(buffer, 0, 1024*8))!=-1){
    out.write(buffer, 0, n);
    count+=n;
   }
   out.flush();
  } catch (IOException e) {
   e.printStackTrace();
  }finally{
   try {
    out.close();
   } catch (IOException e) {
    e.printStackTrace();
   }
   try {
    in.close();
   } catch (IOException e) {
    e.printStackTrace();
   }
  }
  return count;
 }
 
 private final class ProgressReportingOutputStream extends FileOutputStream{
 	 
 public ProgressReportingOutputStream(File file)
    throws FileNotFoundException {
   super(file);
  }
 
  @Override
  public void write(byte[] buffer, int byteOffset, int byteCount)
    throws IOException {
   super.write(buffer, byteOffset, byteCount);
      mProgress += byteCount;
      progress=(int) ((mProgress*100)/length);
    }
  }

@SuppressWarnings("unchecked")
private long unzip(){
  long extractedSize = 0L;
  Enumeration<ZipEntry> entries;
  ZipFile zip = null;
  try {
   zip = new ZipFile(zipFile);
   entries = (Enumeration<ZipEntry>) zip.entries();
   while(entries.hasMoreElements()){
    ZipEntry entry = entries.nextElement();
    System.out.println(entry.getName());
    if(entry.isDirectory()){
     continue;
    }
    File destination = new File(mOutput, entry.getName());
    if(!destination.getParentFile().exists()){
     Log.e(TAG, "make="+destination.getParentFile().getAbsolutePath());
     destination.getParentFile().mkdirs();
    }
    if(destination.exists()&&!mReplaceAll){

    }
    ProgressReportingOutputStream2 outStream = new ProgressReportingOutputStream2(destination);
    extractedSize+=copy2(zip.getInputStream(entry),outStream);
    outStream.close();
   }
  } catch (ZipException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  }finally{
   try {
    zip.close();
   } catch (IOException e) {
    e.printStackTrace();
   }
  }
  return extractedSize;	 
 }
 
 private int copy2(InputStream input, OutputStream output){
  byte[] buffer = new byte[1024*8];
  BufferedInputStream in = new BufferedInputStream(input, 1024*8);
  BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
  int count =0,n=0;
  try {
   while((n=in.read(buffer, 0, 1024*8))!=-1){
    out.write(buffer, 0, n);
    count+=n;
   }
   out.flush();
  } catch (IOException e) {
   e.printStackTrace();
  }finally{
   try {
    out.close();
   } catch (IOException e) {
    e.printStackTrace();
   }
   try {
    in.close();
   } catch (IOException e) {
    e.printStackTrace();
   }
  }
  return count;
 }
 
 private final class ProgressReportingOutputStream2 extends FileOutputStream{
  public ProgressReportingOutputStream2(File file)
    throws FileNotFoundException {
   super(file);
  }
  @Override
  public void write(byte[] buffer, int byteOffset, int byteCount)
    throws IOException {
   super.write(buffer, byteOffset, byteCount);
  }

  
 }
 
 
 }