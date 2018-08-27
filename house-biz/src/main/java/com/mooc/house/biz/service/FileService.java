package com.mooc.house.biz.service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

@Service
public class FileService {
	
	@Value("${file.path}")
	private String filePath;
	
	
	public List<String> getImgPaths(List<MultipartFile> files) {
		String prefix = this.getClass().getResource("/").getPath();
		List<String> paths = Lists.newArrayList();
		files.forEach(file -> {
			File localFile = null;
			if (!file.isEmpty()) {
				try {
					localFile =  saveToLocal(file, prefix+filePath);
					String path = StringUtils.substringAfterLast(localFile.getAbsolutePath(), filePath.substring(1,filePath.length()));
					paths.add(path);
				} catch (IOException e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
		return paths;
	}
	
	public static String getResourcePath(){
	  File file = new File(".");
	  String absolutePath = file.getAbsolutePath();
	  return absolutePath;
	}

	private File saveToLocal(MultipartFile file, String filePath2) throws IOException {
	 File newFile = new File(filePath2 + "/" + Instant.now().getEpochSecond() +"/"+file.getOriginalFilename());
	 if (!newFile.exists()) {
		 newFile.getParentFile().mkdirs();
		 newFile.createNewFile();
	 }
	 Files.write(file.getBytes(), newFile);
     return newFile;
	}

	public static void main(String[] args) {
		String filePath = "/imgs";
		String path = StringUtils.substringAfterLast("E:/imgs/2015/88.png", filePath.substring(1,filePath.length()));

		System.err.println(path);
	}
}
