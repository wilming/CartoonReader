package com.cartoon.util;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {
	private static FileComparator me;
	private static final Collator cp = Collator.getInstance(java.util.Locale.CHINA);
	
	static{
		me = new FileComparator();
	}
	
	private FileComparator(){}
	
	public static FileComparator getInstance(){
		return me;
	}
	
	@Override
	public int compare(File source, File target) {
		return cp.compare(source.getName(), target.getName());
	}

}
