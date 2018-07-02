package com.vin.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * POI 3.17版本excel操作类
 * @author Vincent
 *
 */
public class ExcelUtil {
	
	/************************************XSSF*********************************************/
    
    /**
     * 取得指定单元格行和列
     * @param keyMap 所有单元格行、列集合
     * @param key 单元格标识
     * @return 0：列 1：行（列表型数据不记行，即1无值）
     */
    public static int[] getPos(HashMap keyMap, String key){
        int[] ret = new int[0];
          
        String val = (String)keyMap.get(key);
          
        if(val == null || val.length() == 0)
            return ret;
          
        String pos[] = val.split(",");
          
        if(pos.length == 1 || pos.length == 2){
            ret = new int[pos.length];
            for(int i0 = 0; i0 < pos.length; i0++){
                if(pos[i0] != null && pos[i0].trim().length() > 0){
                    ret[i0] = Integer.parseInt(pos[i0].trim());
                } else {
                    ret[i0] = 0;
                }
            }
        }
        return ret;
    }
      
    /**
     * 取对应格子的值
     * @param sheet
     * @param rowNo 行
     * @param cellNo 列
     * @return
     * @throws IOException
     */
    public static String getCellValue(Sheet sheet,int rowNo,int cellNo) {
        String cellValue = null;
        Row row = sheet.getRow(rowNo);
        Cell cell = row.getCell(cellNo);
        if (cell != null) {
            if (cell.getCellTypeEnum()== CellType.NUMERIC) {
                DecimalFormat df = new DecimalFormat("0");
                cellValue = getCutDotStr(df.format(cell.getNumericCellValue()));
            } else if (cell.getCellTypeEnum() == CellType.STRING) {
                cellValue = cell.getStringCellValue();
            }
            if (cellValue != null) {
                cellValue = cellValue.trim();
            }           
        } else {
            cellValue = null;
        }
        return cellValue;
    }
       
    /**
     * 取整数
     * @param srcString
     * @return
     */
    private static String getCutDotStr(String srcString) {
        String newString = "";
        if (srcString != null && srcString.endsWith(".0")) {
            newString = srcString.substring(0,srcString.length()-2);
        } else {
            newString = srcString;
        }
        return newString;
    }   
      
    /**
     * 读数据模板
     * @param 模板地址
     * @throws IOException
     */
    public static HashMap[] getTemplateFile(String templateFileName) throws IOException {    
        FileInputStream fis = new FileInputStream(templateFileName);
         
        Workbook wbPartModule = null;
        if(templateFileName.endsWith(".xlsx")){
            wbPartModule = new XSSFWorkbook(fis);
        }else if(templateFileName.endsWith(".xls")){
            wbPartModule = new HSSFWorkbook(fis);
        }
        int numOfSheet = wbPartModule.getNumberOfSheets();
        HashMap[] templateMap = new HashMap[numOfSheet];
        for(int i = 0; i < numOfSheet; i++){
            Sheet sheet = wbPartModule.getSheetAt(i);
            templateMap[i] = new HashMap();
            readSheet(templateMap[i], sheet);
        }
        fis.close();
        return templateMap;
    }
    
    /**
     * 读模板数据的样式值置等信息
     * @param keyMap
     * @param sheet
     */
    private static void readSheet(HashMap keyMap, Sheet sheet){
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
          
        for (int j = firstRowNum; j <= lastRowNum; j++) {
            Row rowIn = sheet.getRow(j);
            if(rowIn == null) {
                continue;
            }
            int firstCellNum = rowIn.getFirstCellNum();
            int lastCellNum = rowIn.getLastCellNum();
            for (int k = firstCellNum; k <= lastCellNum; k++) {
//              Cell cellIn = rowIn.getCell((short) k);
                Cell cellIn = rowIn.getCell(k);
                if(cellIn == null) {
                    continue;
                }
                  
                int cellType = cellIn.getCellType();
                if(Cell.CELL_TYPE_STRING != cellType) {
                    continue;
                }
                String cellValue = cellIn.getStringCellValue();
                if(cellValue == null) {
                    continue;
                }
                cellValue = cellValue.trim();
                if(cellValue.length() > 2 && cellValue.substring(0,2).equals("<%")) {
                    String key = cellValue.substring(2, cellValue.length());
                    String keyPos = Integer.toString(k)+","+Integer.toString(j);
                    keyMap.put(key, keyPos);
                    keyMap.put(key+"CellStyle", cellIn.getCellStyle());
                } else if(cellValue.length() > 3 && cellValue.substring(0,3).equals("<!%")) {
                    String key = cellValue.substring(3, cellValue.length());
                    keyMap.put("STARTCELL", Integer.toString(j));
                    keyMap.put(key, Integer.toString(k));
                    keyMap.put(key+"CellStyle", cellIn.getCellStyle());
                }
            }
        }
    }
      
    /**
     * 获取格式，不适于循环方法中使用，wb.createCellStyle()次数超过4000将抛异常
     * @param keyMap
     * @param key
     * @return
     */
    public static CellStyle getStyle(HashMap keyMap, String key,Workbook wb) {
        CellStyle cellStyle = null;      
          
        cellStyle = (CellStyle) keyMap.get(key+"CellStyle");
        //当字符超出时换行
        cellStyle.setWrapText(true);
        CellStyle newStyle = wb.createCellStyle();
        newStyle.cloneStyleFrom(cellStyle);
        return newStyle;
    }
    
    /**
     * 获取单元格的样式
     * @param sheet
     * @param row
     * @param col
     * @return
     */
    public static CellStyle getCellStyle(Sheet sheet,int row,int col) {
    	Row rowIn = sheet.getRow(row);
        Cell cellIn = rowIn.getCell(col);
        CellStyle cellStyle = cellIn.getCellStyle();
        return cellStyle;
    }
    
    /**
     * Excel单元格输出
     * @param sheet
     * @param row 行
     * @param cell 列
     * @param value 值
     * @param cellStyle 样式
     */
    public static void setValue(Sheet sheet, int row, int cell, Object value, CellStyle cellStyle){
        Row rowIn = sheet.getRow(row);
        if(rowIn == null) {
            rowIn = sheet.createRow(row);
        }
        Cell cellIn = rowIn.getCell(cell);
        if(cellIn == null) {
            cellIn = rowIn.createCell(cell);
        }
        if(cellStyle != null) {
            //修复产生多超过4000 cellStyle 异常
            //CellStyle newStyle = wb.createCellStyle();
            //newStyle.cloneStyleFrom(cellStyle);
            cellIn.setCellStyle(cellStyle);
        }
        //对时间格式进行单独处理
        if(value==null){
            //cellIn.setCellValue("");
        }else{
            if (isCellDateFormatted(cellStyle)) {
                cellIn.setCellValue((Date) value);
            } else {
            	
            	if(value instanceof Float){
            		cellIn.setCellValue(new BigDecimal(value.toString()).doubleValue());
            	}else if(value instanceof Double){
            		cellIn.setCellValue(new BigDecimal(value.toString()).doubleValue());
            	}else if(value instanceof String){
            		cellIn.setCellValue(value.toString());
            	}else if(value instanceof Long){
            		cellIn.setCellValue(Long.valueOf(value.toString()));
            	}else if(value instanceof Integer){
            		cellIn.setCellValue(Integer.valueOf(value.toString()));
            	}else if(value instanceof BigDecimal){
            		cellIn.setCellValue(((BigDecimal) value).doubleValue());
            	}else{
            		cellIn.setCellValue(value.toString());
            	}
            }
        }
    }
    
    /**
     * 设置单元格公式
     * @param sheet
     * @param row
     * @param cell
     * @param value
     * @param cellStyle
     */
    public static void setFormula(Sheet sheet, int row, int cell, String value, CellStyle cellStyle){
        Row rowIn = sheet.getRow(row);
        if(rowIn == null) {
            rowIn = sheet.createRow(row);
        }
        Cell cellIn = rowIn.getCell(cell);
        if(cellIn == null) {
            cellIn = rowIn.createCell(cell);
        }
        if(cellStyle != null) {
            //修复产生多超过4000 cellStyle 异常
            //CellStyle newStyle = wb.createCellStyle();
            //newStyle.cloneStyleFrom(cellStyle);
            cellIn.setCellStyle(cellStyle);
        }
        
        // 设置单元格类型为公式
        cellIn.setCellType(CellType.FORMULA);
        
        if(value==null){
            //cellIn.setCellValue("");
        }else{
        	cellIn.setCellFormula(value);
        }
    }
    
    /**
     * 获取单元格公式
     * @param sheet
     * @param rowNo
     * @param cellNo
     * @return
     */
    public static String getCellFormula(Sheet sheet,int rowNo,int cellNo) {
        Row row = sheet.getRow(rowNo);
        Cell cell = row.getCell(cellNo);
        return cell.getCellFormula();
    }
    
    /**
     * 根据表格样式判断是否为日期格式
     * @param cellStyle
     * @return
     */
    public static boolean isCellDateFormatted(CellStyle cellStyle){
        if(cellStyle==null){
            return false;
        }
        int i = cellStyle.getDataFormat();
        String f = cellStyle.getDataFormatString();
         
        return org.apache.poi.ss.usermodel.DateUtil.isADateFormat(i, f);
    }
    /**
     * 适用于导出的数据Excel格式样式重复性较少
     * 不适用于循环方法中使用
     * @param wbModule
     * @param sheet
     * @param pos 模板文件信息
     * @param startCell 开始的行
     * @param value 要填充的数据
     * @param cellStyle 表格样式
     */
    public static void createCell(Workbook wbModule, Sheet sheet,HashMap pos, int startCell,Object value,String cellStyle){
        int[] excelPos = getPos(pos, cellStyle);
        setValue(sheet, startCell, excelPos[0], value, getStyle(pos, cellStyle,wbModule));
    }
    /************************************XSSF*******************************************/
    
    /************************************************************************************/
    
    /**
     * 获取templateFileName 路径 excel信息
     * @param templateFileName
     * @return
     * @throws IOException
     */
    public static Workbook getWorkBook(String templateFileName) throws IOException {
    	FileInputStream fis = new FileInputStream(templateFileName);
        Workbook wbPartModule = null;
        
        if(templateFileName.endsWith(".xlsx")){
            wbPartModule = new XSSFWorkbook(fis);
        }else if(templateFileName.endsWith(".xls")){
            wbPartModule = new HSSFWorkbook(fis);
        }
        fis.close();
        return wbPartModule;
    }
    
    public static Workbook getWorkBook(InputStream fis,String templateFileType) throws IOException {
        Workbook wbPartModule = null;
        if(templateFileType.endsWith(".xlsx")){
            wbPartModule = new XSSFWorkbook(fis);
        }else if(templateFileType.endsWith(".xls")){
           // POIFSFileSystem fs = new POIFSFileSystem(fis);
            wbPartModule = new HSSFWorkbook(fis);
        }
        fis.close();
        return wbPartModule;
    }
    
    /**
     * 设置表格样式
     * @param isWrap 是否自动换行  true:自动换行 , false:不自动换行
     * @param isBorder 是否需要边框 true: 需要  ， false: 不需要
     * @param alignment 水平对齐方式   1：靠左， 2：靠右，  3：居中， 其他：靠左
     * @param vertical  垂直对齐方式   1：靠上， 2：靠下， 3：居中 ， 其他：靠上
     * @param font 字体
     * @param wb Workbook
     * @return
     */
    public static CellStyle setCellStyle(boolean isWrap,boolean isBorder,int alignment,
    		int vertical,Font font,Workbook wb){
    	 CellStyle cellStyle = wb.createCellStyle();
    	 
    	 // 是否有边框
    	 if(isBorder){
    		 cellStyle.setBorderBottom(BorderStyle.THIN);
    		 cellStyle.setBorderLeft(BorderStyle.THIN);
    		 cellStyle.setBorderRight(BorderStyle.THIN);
    		 cellStyle.setBorderTop(BorderStyle.THIN);
    	 }
    	 
    	 // 水平对齐
    	 if(alignment == 1){
    		 // 靠左
        	 cellStyle.setAlignment(HorizontalAlignment.LEFT);
    	 }else if(alignment == 2){
    		// 靠右
        	cellStyle.setAlignment(HorizontalAlignment.RIGHT);
    	 }else if(alignment == 3){
    		 // 居中
    		 cellStyle.setAlignment(HorizontalAlignment.CENTER);
    	 }else {
    		// 靠左
        	cellStyle.setAlignment(HorizontalAlignment.LEFT);
    	 }
    	 
    	 // 垂直对齐
    	 if(vertical == 1){
    		 // 靠上
        	 cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
    	 }else if(vertical == 2){
    		// 靠下
        	cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
    	 }else if(vertical == 3){
    		 // 居中
    		 cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    	 }else {
    		// 靠上
        	cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
    	 }
    	 
    	 // 是否自动换行
    	 if(isWrap){
    		// 自动换行
    		 cellStyle.setWrapText(true);
    	 }else {
    		 cellStyle.setWrapText(false);
    	 }
    	 
    	 if(font != null){
    		 // 设置字体
    		 cellStyle.setFont(font);
    	 }
    	 
    	 return cellStyle;
    }
    
    /**
     * 设置单元格的样式
     * @param sheet
     * @param row
     * @param cell
     * @param cellStyle
     */
    public static void setCellStyle(Sheet sheet,int row,int cell,CellStyle cellStyle){
    	Row rowIn = sheet.getRow(row);
        if(rowIn == null) {
            rowIn = sheet.createRow(row);
        }
        Cell cellIn = rowIn.getCell(cell);
        if(cellIn == null) {
            cellIn = rowIn.createCell(cell);
        }
        if(cellStyle != null) {
            //修复产生多超过4000 cellStyle 异常
            //CellStyle newStyle = wb.createCellStyle();
            //newStyle.cloneStyleFrom(cellStyle); // 
            cellIn.setCellStyle(cellStyle);
        }
    }
    
    /**
     * 導出excel
     * @param wb
     * @param out
     * @return
     */
    public static boolean exportExcel(Workbook wb,OutputStream out){
    	boolean isSuccess = true;
    	try
    	{
    		wb.write(out);
    		out.flush();
    	}catch(IOException e){
            isSuccess = false;
    	}finally{
    		try {
                out.close();
            } catch (IOException e) {
                isSuccess = false;
            }
    	}
    	
    	return isSuccess;
    }
    
    /**
     * 图片的插入
     * @param wb
     * @param patriarch
     * @param dx1 范围：0 ~ 1023
     * @param dy1 范围：0 ~ 255
     * @param dx2 范围：0 ~ 1023
     * @param dy2 范围：0 ~ 255
     * @param col1 范围：0 ~ 255
     * @param row1 范围：0 ~ 255*256
     * @param col2 范围：0 ~ 255
     * @param row2 范围：0 ~ 255*256
     * @param picPath 图片路径
     */
    public static void addPic(Workbook wb,HSSFPatriarch patriarch,int dx1,int dy1,int dx2,int dy2,
    		short col1,int row1,short col2,int row2,String picPath){
    	
    	BufferedImage bufferImg = null;
    	try {
    		File picFile = new File(picPath);
    		if(!picFile.exists()){
    			return;
    		}
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            bufferImg = ImageIO.read(picFile);
            ImageIO.write(bufferImg, "jpg", byteArrayOut);
              
            //anchor主要用于设置图片的属性
            HSSFClientAnchor anchor = new HSSFClientAnchor(dx1, dy1, dx2, dy2,col1, row1, col2, row2);
            anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
            //创建图片数据对象
            int pictureIndex = wb.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG);
            //插入图片
            patriarch.createPicture(anchor, pictureIndex);
    	}catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 添加图片
     * @param wb
     * @param patriarch
     * @param dx1
     * @param dy1
     * @param dx2
     * @param dy2
     * @param col1
     * @param row1
     * @param col2
     * @param row2
     * @param imgByteArray  图片二进制流
     */
    public static void addPic(Workbook wb,HSSFPatriarch patriarch,int dx1,int dy1,int dx2,int dy2,
    		short col1,int row1,short col2,int row2,byte[] imgByteArray){
    	
    	try {
            //anchor主要用于设置图片的属性
            HSSFClientAnchor anchor = new HSSFClientAnchor(dx1, dy1, dx2, dy2,col1, row1, col2, row2);
            anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
            //创建图片数据对象
            int pictureIndex = wb.addPicture(imgByteArray, HSSFWorkbook.PICTURE_TYPE_JPEG);
            //插入图片
            patriarch.createPicture(anchor, pictureIndex);
    	}catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /** 
     * insert row into the target sheet, the style of cell is the same as starRow 
     * @param sheet 
     * @param starRow - the row to start shifting 
     * @param rows 插入行数
     */  
     public static void insertRow(Sheet sheet, int starRow,int rows) {  
          // 移出 rows 行的 空间
    	 if(starRow < sheet.getLastRowNum()){
             sheet.shiftRows(starRow + 1, sheet.getLastRowNum(), rows,true,false);
    	 }
          //  Parameters:  
          //   starRow - the row to start shifting  
          //   endRow - the row to end shifting  
          //   n - the number of rows to shift  
          //   copyRowHeight - whether to copy the row height during the shift  
          //   resetOriginalRowHeight - whether to set the original row's height to the default  
  
		starRow = starRow - 1;

		for (int i = 0; i < rows; i++) {

			Row sourceRow = null;
			Row targetRow = null;
			Cell sourceCell = null;
			Cell targetCell = null;
			short m;

			starRow = starRow + 1;
			sourceRow = sheet.getRow(starRow);
			targetRow = sheet.createRow(starRow + 1);
			targetRow.setHeight(sourceRow.getHeight());

			for (m = sourceRow.getFirstCellNum(); m < sourceRow.getLastCellNum(); m++) {
				sourceCell = sourceRow.getCell(m);
				targetCell = targetRow.createCell(m);
				targetCell.setCellStyle(sourceCell.getCellStyle());
				//targetCell.setCellType(sourceCell.getCellType()); // poi3.15以前
				targetCell.setCellType(sourceCell.getCellTypeEnum());
			}
		}
     }
     
     /**
      * 复制行
      * @param sheet 
      * @param sourceIndex 源行行号
      * @param targetIndex 目标行行号
      */
     public static void copyRow(Sheet sheet, int sourceIndex,int targetIndex) {
    	 
		Row sourceRow = null;
		Row targetRow = null;
		Cell sourceCell = null;
		Cell targetCell = null;
		short m;
		
		sourceRow = sheet.getRow(sourceIndex);
		targetRow = sheet.createRow(targetIndex);
		targetRow.setHeight(sourceRow.getHeight());

		for (m = sourceRow.getFirstCellNum(); m < sourceRow.getLastCellNum(); m++) {
			sourceCell = sourceRow.getCell(m);
			targetCell = targetRow.createCell(m);
			targetCell.setCellValue(sourceCell.getStringCellValue());
			targetCell.setCellStyle(sourceCell.getCellStyle());
			//targetCell.setCellType(sourceCell.getCellType());// poi3.15以前
			targetCell.setCellType(sourceCell.getCellTypeEnum());
		}
    }
    
     /**
      * 复制行（可以是多行）
      * @param sheet 表sheet
      * @param startRow 复制起始行
      * @param endRow 复制结束行
      * @param pPosition 复制的位置
      */
     public static void copyRows(Sheet sheet,int startRow, int endRow, int pPosition) {
		int pStartRow = startRow - 1;
		int pEndRow = endRow - 1;
		int targetRowFrom;
		int targetRowTo;
		int columnCount;
		CellRangeAddress region = null;
		int i;
		int j;
		if (pStartRow == -1 || pEndRow == -1) {
			return;
		}
		// 拷贝合并的单元格
		for (i = 0; i < sheet.getNumMergedRegions(); i++) {
			region = sheet.getMergedRegion(i);
			if ((region.getFirstRow() >= pStartRow)
					&& (region.getLastRow() <= pEndRow)) {
				targetRowFrom = region.getFirstRow() - pStartRow + pPosition;
				targetRowTo = region.getLastRow() - pStartRow + pPosition;
				CellRangeAddress newRegion = region.copy();
				newRegion.setFirstRow(targetRowFrom);
				newRegion.setFirstColumn(region.getFirstColumn());
				newRegion.setLastRow(targetRowTo);
				newRegion.setLastColumn(region.getLastColumn());
				sheet.addMergedRegion(newRegion);
			}
		}
		// 设置列宽
		for (i = pStartRow; i <= pEndRow; i++) {
			Row sourceRow = sheet.getRow(i);
			columnCount = sourceRow.getLastCellNum();
			if (sourceRow != null) {
				Row newRow = sheet.createRow(pPosition - pStartRow + i);
				newRow.setHeight(sourceRow.getHeight());
				for (j = 0; j < columnCount; j++) {
					Cell templateCell = sourceRow.getCell(j);
					if (templateCell != null) {
						Cell newCell = newRow.createCell(j);
						copyCell(templateCell, newCell);
					}
				}
			}
		}
	}

	private static void copyCell(Cell srcCell, Cell distCell) {
		distCell.setCellStyle(srcCell.getCellStyle());
		if (srcCell.getCellComment() != null) {
			distCell.setCellComment(srcCell.getCellComment());
		}
		int srcCellType = srcCell.getCellType();
		distCell.setCellType(srcCellType);
		if (srcCellType == Cell.CELL_TYPE_NUMERIC) {
			if (DateUtil.isCellDateFormatted(srcCell)) {
				distCell.setCellValue(srcCell.getDateCellValue());
			} else {
				distCell.setCellValue(srcCell.getNumericCellValue());
			}
		} else if (srcCellType == Cell.CELL_TYPE_STRING) {
			distCell.setCellValue(srcCell.getRichStringCellValue());
		} else if (srcCellType == Cell.CELL_TYPE_BLANK) {
			// nothing21
		} else if (srcCellType == Cell.CELL_TYPE_BOOLEAN) {
			distCell.setCellValue(srcCell.getBooleanCellValue());
		} else if (srcCellType == Cell.CELL_TYPE_ERROR) {
			distCell.setCellErrorValue(srcCell.getErrorCellValue());
		} else if (srcCellType == Cell.CELL_TYPE_FORMULA) {
			distCell.setCellFormula(srcCell.getCellFormula());
		} else { // nothing29

		}
	}
 // HSSFSheet
 /*public void copyRows(int startRow, int endRow, int pPosition,
			HSSFSheet sheet) {
		int pStartRow = startRow - 1;
		int pEndRow = endRow - 1;
		int targetRowFrom;
		int targetRowTo;
		int columnCount;
		CellRangeAddress region = null;
		int i;
		int j;
		if (pStartRow == -1 || pEndRow == -1) {
			return;
		}
		// 拷贝合并的单元格
		for (i = 0; i < sheet.getNumMergedRegions(); i++) {
			region = sheet.getMergedRegion(i);
			if ((region.getFirstRow() >= pStartRow)
					&& (region.getLastRow() <= pEndRow)) {
				targetRowFrom = region.getFirstRow() - pStartRow + pPosition;
				targetRowTo = region.getLastRow() - pStartRow + pPosition;
				CellRangeAddress newRegion = region.copy();
				newRegion.setFirstRow(targetRowFrom);
				newRegion.setFirstColumn(region.getFirstColumn());
				newRegion.setLastRow(targetRowTo);
				newRegion.setLastColumn(region.getLastColumn());
				sheet.addMergedRegion(newRegion);
			}
		}
		// 设置列宽
		for (i = pStartRow; i <= pEndRow; i++) {
			HSSFRow sourceRow = sheet.getRow(i);
			columnCount = sourceRow.getLastCellNum();
			if (sourceRow != null) {
				HSSFRow newRow = sheet.createRow(pPosition - pStartRow + i);
				newRow.setHeight(sourceRow.getHeight());
				for (j = 0; j < columnCount; j++) {
					HSSFCell templateCell = sourceRow.getCell(j);
					if (templateCell != null) {
						HSSFCell newCell = newRow.createCell(j);
						copyCell(templateCell, newCell);
					}
				}
			}
		}
	}


private void copyCell(HSSFCell srcCell, HSSFCell distCell) {
		distCell.setCellStyle(srcCell.getCellStyle());
		if (srcCell.getCellComment() != null) {
			distCell.setCellComment(srcCell.getCellComment());
		}
		int srcCellType = srcCell.getCellType();
		distCell.setCellType(srcCellType);
		if (srcCellType == HSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(srcCell)) {
				distCell.setCellValue(srcCell.getDateCellValue());
			} else {
				distCell.setCellValue(srcCell.getNumericCellValue());
			}
		} else if (srcCellType == HSSFCell.CELL_TYPE_STRING) {
			distCell.setCellValue(srcCell.getRichStringCellValue());
		} else if (srcCellType == HSSFCell.CELL_TYPE_BLANK) {
			// nothing21
		} else if (srcCellType == HSSFCell.CELL_TYPE_BOOLEAN) {
			distCell.setCellValue(srcCell.getBooleanCellValue());
		} else if (srcCellType == HSSFCell.CELL_TYPE_ERROR) {
			distCell.setCellErrorValue(srcCell.getErrorCellValue());
		} else if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {
			distCell.setCellFormula(srcCell.getCellFormula());
		} else { // nothing29

		}
	}*/
    
	/**
	 * 获取workbook的inputstream
	 * @param wb
	 * @return
	 */
	public static InputStream getInputStreamData(Workbook wb) {
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
        	wb.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        byte[] content = os.toByteArray();
        
        return new ByteArrayInputStream(content);
	}
}
