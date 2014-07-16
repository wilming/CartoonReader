package com.comic.xmlparser;

public class note {
	private int id;
    private int pagenum;
    private String name;
    private String size;
    private String sumary;
    private String url;
    private String imgsrc;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPagenum() {
        return pagenum;
    }
    public void setPagenum(int pagenum) {
        this.pagenum = pagenum;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getImgsrc() {
        return imgsrc;
    }
    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }
    public String getSumary() {
        return sumary;
    }
    public void setSumary(String sumary) {
        this.sumary = sumary;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + pagenum;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        note other = (note) obj;
        if (name != other.name)
            return false;
        return true;
    }
    
}