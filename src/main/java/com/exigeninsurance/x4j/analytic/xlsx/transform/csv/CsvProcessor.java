/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.csv;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.exigeninsurance.x4j.analytic.api.ReportContext;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.Node;
import com.exigeninsurance.x4j.analytic.xlsx.core.node.TableNode;
import com.exigeninsurance.x4j.analytic.xlsx.transform.SheetParser;
import com.exigeninsurance.x4j.analytic.xlsx.transform.WorkbookProcessor;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXStylesTable;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLSXWorkbook;
import com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx.XLXContext;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroNodeFactoryImpl;
import com.exigeninsurance.x4j.analytic.xlsx.utils.MacroParser;




public class CsvProcessor extends WorkbookProcessor {
	
    
	private final OutputStream out;
	private final XLSXWorkbook workBook;
	private final XLSXStylesTable styles;

	public CsvProcessor(XLSXWorkbook workBook, XLSXStylesTable styles,
			XLSXStylesTable defaultStyles, OutputStream out) {
		this.out = out;
		this.styles = styles;		
		this.workBook = workBook;
	}

	public void processSheets(	ReportContext reportContext, List<String> savedParts	)
	throws  Exception {
		for(int i = 0; i < workBook.getNumberOfSheets(); i++){
			nextSheet(reportContext, savedParts, i);
		}
	}

	private void nextSheet(
			ReportContext reportContext,
			List<String> savedParts,
			int index
	)
	throws  Exception {
		XSSFSheet sheet = workBook.getSheetAt(index);

		SheetParser parser = new CsvSheetParser(sheet,styles,reportContext);
		
        parser.setMacroParser(new MacroParser(sheet, new MacroNodeFactoryImpl(sheet)));
		Node root = parser.parse(sheet);

		XLXContext context = new XLXContext(null,sheet,reportContext,out);
		context.setFormatProvider(getFormatProvider());
		context.setStyles(styles);
		context.setDataProvider(getDataProvider());
		context.setTemplateProvider(getTemplateProvider());
		trimChildren(root);
		root.process(context);
		context.flush();
	}
	
	private void trimChildren(Node root) {
		List<Node> newChildren = new ArrayList<Node>();
		int max = 0;
		for (Node child : root.getChildren()) {
			if (child instanceof TableNode) {
				if (max < child.getChildren().get(0).getChildren().size()) {
					newChildren.clear();
					newChildren.add(child);
				}
			}
		}
		root.setChildren(newChildren);
		
	}

	
}
