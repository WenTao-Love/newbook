package com.szlib.seeker;

import java.util.Date;

public class NewBook {
	private String biblisomtableid;
	private String biblisommetaid;
	private String ordcatamid;
	
	private String title;
	private String author;
	private String publisher_name;
	private String publisher_date;
	private Date publisher_time;
	private String abstract_self;
	private String isbn;
	private String price;
	
	private String checkUrl;
	private String readerAccessUrl;
	private String addExpressUrl;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublisher_name() {
		return publisher_name;
	}

	public void setPublisher_name(String publisher_name) {
		this.publisher_name = publisher_name;
	}

	public String getPublisher_date() {
		return publisher_date;
	}

	public void setPublisher_date(String publisher_date) {
		this.publisher_date = publisher_date;
	}

	public String getAbstract_self() {
		return abstract_self;
	}

	public void setAbstract_self(String abstract_self) {
		this.abstract_self = abstract_self;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Date getPublisher_time() {
		return publisher_time;
	}

	public void setPublisher_time(Date publisher_time) {
		this.publisher_time = publisher_time;
	}

	public String getBiblisomtableid() {
		return biblisomtableid;
	}

	public void setBiblisomtableid(String biblisomtableid) {
		this.biblisomtableid = biblisomtableid;
	}

	public String getBiblisommetaid() {
		return biblisommetaid;
	}

	public void setBiblisommetaid(String biblisommetaid) {
		this.biblisommetaid = biblisommetaid;
	}

	public String getOrdcatamid() {
		return ordcatamid;
	}

	public void setOrdcatamid(String ordcatamid) {
		this.ordcatamid = ordcatamid;
	}

	public String getCheckUrl() {
		return checkUrl;
	}

	public void setCheckUrl(String checkUrl) {
		this.checkUrl = checkUrl;
	}

	public String getReaderAccessUrl() {
		return readerAccessUrl;
	}

	public void setReaderAccessUrl(String readerAccessUrl) {
		this.readerAccessUrl = readerAccessUrl;
	}

	public String getAddExpressUrl() {
		return addExpressUrl;
	}

	public void setAddExpressUrl(String addExpressUrl) {
		this.addExpressUrl = addExpressUrl;
	}
	
}
