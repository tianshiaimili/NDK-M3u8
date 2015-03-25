package com.tvb.smartdownload.packetizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.SparseArray;

import com.tvb.smartdownload.utils.LogUtils;

public class PacketizerUtils {

	/**存放全部的分割后的tss文件路径*/
	private List<String> pathLists ;
	
	/**把每个ts分割后的路径 放入到一个小集合中，按照test0-1.tss,test0-2.tss,test0-3.tss,test0r.ts的顺序*/
	private List<SparseArray<String>> cutPathLists;
	
	private static PacketizerUtils packetizerUtils = null;
	
	public PacketizerUtils (){
		
		pathLists = new ArrayList<String>();
		cutPathLists = new ArrayList<SparseArray<String>>();
		
	}
	
	
	public static PacketizerUtils instance(){
		
		if(packetizerUtils == null){
			
			packetizerUtils = new PacketizerUtils();
			
		}
		return packetizerUtils;
	}
	
	
	/**
	 * 复原切割的文件
	 * 
	 * @param strip0
	 *            切割分成三部分 的第一份
	 * @param srcFile
	 *            复原的新的读取文件
	 * 
	 */
	public native int mergeStripsWithMD5(String strip0, String strip1,
			String strip2, String srcFile);
	
	
	/**
	 * @param srcFile
	 *            要切割的源文件路径
	 * @param strip0
	 *            切割分成三部分 的第一份
	 * */
	public native int splitToStripsWithMD5Min(String srcFile, String strip0,
			String strip1, String strip2);
	
	
	/**
	 * get the recovery .ts file path
	 * @param stripName the Strip.tss file path
	 * @return
	 */
	public static String recoveryNameByStrip(String stripName){
		String completePath = null;
		if(stripName != null){
			
			int lineIndex = stripName.lastIndexOf("/");
			int horizontalLineIndex = stripName.lastIndexOf("-");
			String name = stripName.substring(lineIndex+1,horizontalLineIndex)+".ts";
			String header = stripName.substring(0, lineIndex+1);
			completePath = header+name;
			LogUtils.d("the completePath =="+completePath);
			return completePath;
		}
		
		return null;
		
	}
	
	/**
	 * 复原 文件
	 * @param fileFolder the tss folder path
	 * 
	 */
	public void recoveryFile(String fileFolder){
		String temFileName= null;
		String cutName = null;
		File file = new File(fileFolder);
		if(!file.exists()){
			return;
		}
		File[] files = file.listFiles();
		if(files !=null && files.length > 0){
			
			for(File tempFile : files){
				
				temFileName = tempFile.getPath();
				if(temFileName.endsWith("ts")){
					continue;
				}
				if(temFileName.equals("m3u8")){
					continue;
				}
				pathLists.add(temFileName);
			}
			
		}else {
			return;
		}
		
//		fileNameArray.
		Collections.sort(pathLists);
		LogUtils.e("the pathLists.size = "+pathLists.size());
		
		SparseArray<String> tempArray =null;
		for(int k = 0;k< pathLists.size() ;k++){
			
		LogUtils.i("__________________"+k);
			
			if(k % 3 == 0){
				LogUtils.d("the k == "+k);
				tempArray = new SparseArray<String>();
			}
			
			tempArray.put(k%3, pathLists.get(k));
			
			if((k+1) % 3 == 0){
				LogUtils.d("the k is =="+k);
				cutPathLists.add(tempArray);
			}
		}

		///
		if(cutPathLists != null && cutPathLists.size() > 0 ){
			
			String recoveryName= null;
//			List<SparseArray<String>> paths = (List<SparseArray<String>>) msg.obj;
			LogUtils.d("the paths.size = "+cutPathLists.size());
			for(SparseArray<String> temArray : cutPathLists){
				
				recoveryName = recoveryNameByStrip(temArray.get(0));
				
				if(recoveryName != null){
					LogUtils.e("+++++++++++++++++++");
					packetizerUtils.mergeStripsWithMD5(temArray.get(0),temArray.get(1),temArray.get(2),recoveryName);
					
				}
				
			}
			
		}
		
	
		////deleted tss file
		LogUtils.e("deleted file .......");
//		deleteAllTssFile(fileFolder);
		
		
	}
	
	/**
	 * 
	 * @param path_Array the ts array
	 * @param filePath   the folder path where to save the tss file
	 */
	public void cutFileToTss(String[] path_Array,String filePath){
		String stripTempName= null;
		File file = null;

		if(path_Array != null && path_Array.length > 0 && filePath != null){
			
			for (String path : path_Array) {
//				LogUtils.i("***********************path --"+path);
//				LogUtils.i("***********************getFileName --"+getFileName(path));
				stripTempName = getFileName(path);
				String writeFile = splitToStripsWithMD5Min(path, filePath+stripTempName+"-1.tss",
						filePath+stripTempName+"-2.tss", filePath+stripTempName+"-3.tss") + "";
				LogUtils.e("the writeFile = " + writeFile);
				
				file = new File(path);
				file.delete();
				
			}
			
		}
		
	}
	
	/**
	 * 
	 * @param path_Array the ts array
	 * @param filePath   the folder path where to save the tss file
	 */
	public void cutFileToTss(List<String> path_Array,String filePath){
		String stripTempName= null;
		File file = null;
		if(path_Array != null && path_Array.size() > 0 && filePath != null){
			
			for (String path : path_Array) {
//				LogUtils.i("***********************path --"+path);
//				LogUtils.i("***********************getFileName --"+getFileName(path));
				stripTempName = getFileName(path);
				String writeFile = splitToStripsWithMD5Min(path, filePath+stripTempName+"-1.tss",
						filePath+stripTempName+"-2.tss", filePath+stripTempName+"-3.tss") + "";
				LogUtils.e("the writeFile = " + writeFile);
				
				file = new File(path);
				file.delete();
				
			}
			
		}
		
	}
	
	
	/**
	 * 
	 * @param tsPath the ts path
	 * @param filePath   the folder path where to save the tss file
	 */
	public void cutFileToTss(String tsPath,String filePath){
		String stripTempName= null;
		if(tsPath != null  && filePath != null){
			
//				LogUtils.i("***********************path --"+path);
//				LogUtils.i("***********************getFileName --"+getFileName(path));
				stripTempName = getFileName(tsPath);
				String writeFile = splitToStripsWithMD5Min(tsPath, filePath+stripTempName+"-1.tss",
						filePath+stripTempName+"-2.tss", filePath+stripTempName+"-3.tss") + "";
				LogUtils.e("the writeFile = " + writeFile);
				
				File file = new File(tsPath);
				file.delete();
				
		}
	}
	
	
	/**
	 * 根据源文件获取生成切割文件名
	 * @param tempName
	 * @return
	 */
	public String getFileName(String tempName){
		String name = null;
		if(tempName != null){
			int index = tempName.lastIndexOf("/");
			name = tempName.substring(index+1, tempName.length() - 3);
			LogUtils.d("name = "+name);
			return name;
		}
		return null;
	}
	
	/**deleted all the Strip tss file
	 * 
	 * @param fileFolder the Strip tss folder path where they are 
	 */
	public void deleteAllTssFile(String fileFolder){
		LogUtils.e("deleteAllTssFile...........");
		String temFileName= null;
		String cutName = null;
		File file = new File(fileFolder);
		if(!file.exists()){
			return;
		}
		File[] files = file.listFiles();
		if(files !=null && files.length > 0){
			
			for(File tempFile : files){
				temFileName = tempFile.getPath();
				if(!temFileName.endsWith("ts")){
					continue;
				}
				tempFile.delete();
			}
			
		}else {
			return;
		}
		
	}
	
	
}
