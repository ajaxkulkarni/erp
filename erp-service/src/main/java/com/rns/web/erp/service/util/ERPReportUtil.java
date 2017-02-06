package com.rns.web.erp.service.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;

import com.itextpdf.text.Document;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;

public class ERPReportUtil {
	
	public static void writePdf() {
		try {
		    String contentPath = "email/subscription_mail.html";
			String k = CommonUtils.readFile(contentPath);
		    OutputStream file = new FileOutputStream(new File("Test.pdf"));
		    Document document = new Document();
		    PdfWriter.getInstance(document, file);
		    document.open();
		    HTMLWorker htmlWorker = new HTMLWorker(document);
		    htmlWorker.parse(new StringReader(k));
		    document.close();
		    file.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

}
