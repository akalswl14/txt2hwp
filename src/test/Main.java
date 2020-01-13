package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.control.Control;
import kr.dogfoot.hwplib.object.bodytext.control.ControlColumnDefine;
import kr.dogfoot.hwplib.object.bodytext.control.ControlType;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.object.bodytext.paragraph.charshape.CharPositionShapeIdPair;
import kr.dogfoot.hwplib.object.bodytext.paragraph.charshape.ParaCharShape;
import kr.dogfoot.hwplib.object.bodytext.paragraph.header.DivideSort;
import kr.dogfoot.hwplib.object.bodytext.paragraph.header.ParaHeader;
import kr.dogfoot.hwplib.object.bodytext.paragraph.lineseg.LineSegItem;
import kr.dogfoot.hwplib.object.bodytext.paragraph.lineseg.ParaLineSeg;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.ParaText;
import kr.dogfoot.hwplib.object.docinfo.CharShape;
import kr.dogfoot.hwplib.object.docinfo.DocInfo;
import kr.dogfoot.hwplib.reader.HWPReader;
import kr.dogfoot.hwplib.reader.docinfo.ForCharShape;
import kr.dogfoot.hwplib.tool.paragraphadder.docinfo.CharShapeAdder;
import kr.dogfoot.hwplib.tool.paragraphadder.docinfo.DocInfoAdder;
import kr.dogfoot.hwplib.writer.HWPWriter;



public class Main {

	public static void main(String[] args) throws Exception {
		String filepath = "/Users/carly/development/txt2hwp/empty.hwp";
		HWPFile hwpFile = HWPReader.fromFile(filepath);
        
		// 단 나누기 적용중.
//		Paragraph p = hwpFile.getBodyText().getSectionList().get(0).getParagraph(0);
//		ControlColumnDefine col = (ControlColumnDefine) p.addNewControl(ControlType.ColumnDefine);
//		col.getHeader().getProperty().setColumnCount((short) 2);
//		System.out.println(col.getHeader().getProperty().getColumnCount());
		
		
//		String filepath1 = "/Users/carly/eclipse-workspace/test/onTablet.hwp";
//		HWPFile hwpFile1 = HWPReader.fromFile(filepath1);
//		Paragraph p1 = hwpFile1.getBodyText().getSectionList().get(0).getParagraph(0);
//		// getControlList()했더니 2개가나왔다.첫번쨰가 section정보, 두번째가 columndefine정보여서 1을 인덱스로 정함.
//		ControlColumnDefine col1 = (ControlColumnDefine) p1.getControlList().get(1);
////		ControlColumnDefine col1 = (ControlColumnDefine) p1.addNewControl(ControlType.ColumnDefine);
//		System.out.println(col1.getHeader().getProperty().getColumnCount());
		
		
		// 파일 쓰기
        if (hwpFile != null) {
        	Section s = hwpFile.getBodyText().getSectionList().get(0);
            Paragraph firstParagraph = s.getParagraph(0);
            
            //다단 설정
            ControlColumnDefine col = (ControlColumnDefine) firstParagraph.getControlList().get(1);         
    		col.getHeader().getProperty().setColumnCount((short) 2);
    		col.getHeader().setGapBetweenColumn(2000);
    		
//    		ControlColumnDefine col = (ControlColumnDefine) firstParagraph.addNewControl(ControlType.ColumnDefine);
//    		System.out.println(col.getHeader().getProperty().getColumnCount());
    		
            //파일 객체 생성
            File file = new File("/Users/carly/eclipse-workspace/test/text3.txt");
            //입력 스트림 생성
            FileReader filereader = new FileReader(file);
            //입력 버퍼 생성
            BufferedReader bufReader = new BufferedReader(filereader);
            String line = "";
            int firstline = 1;
            String prestr = "";
            DocInfo doc = hwpFile.getDocInfo();
            
            //볼드체 적용
            //charshape 추가 및 설정.
            int charShapeIndexForBold = createNewCharShape(doc);
            setBold(firstParagraph, charShapeIndexForBold);
            
            while((line = bufReader.readLine()) != null){ 
            	String tmp = line;
            	int len = tmp.length();
            	if(len == 0) {
            		tmp = "";
            	}
            	if(firstline==1) {
            		firstParagraph.getText().addString("○"+tmp);
            		Paragraph pre = firstParagraph;
            		firstParagraph = s.addNewParagraph();
            		//들여쓰기를 "  "로 대신함.
            		setNewParagraph(firstParagraph, "  ", pre,false);
            		firstline = 0;
            		prestr = tmp;
            	}
            	else if(len!=0 &&(prestr=="")) {	
            		Paragraph pre = firstParagraph;
            		firstParagraph = s.addNewParagraph();
            		setNewParagraph(firstParagraph, "", pre,true);
            		setBold(firstParagraph, charShapeIndexForBold);
            		firstParagraph.getText().addString("○"+tmp);
            		firstParagraph = s.addNewParagraph();
            		//들여쓰기를 "  "로 대신함.
            		setNewParagraph(firstParagraph, "   ", pre,false);
            	}else {
                	firstParagraph.getText().addString(tmp);

            	}
            	prestr = tmp;
            }
            //.readLine()은 끝에 개행문자를 읽지 않는다.            
            bufReader.close();
            String writePath = "/Users/carly/eclipse-workspace/test/getTxt2.hwp";
            HWPWriter.toFile(hwpFile, writePath);

        }

	}
	private static void setBold(Paragraph firstParagraph, int charShapeIndexForBold) {
		ParaCharShape pcs = firstParagraph.getCharShape();
        pcs.addParaCharShape(0, charShapeIndexForBold);
        //pcs.getPositonShapeIdPairList()에 쌍을 추가함.
	}
	private static int createNewCharShape(DocInfo doc) {
		
		//CharShape를 추가한뒤, 기존에 있던 cs와 bold부분을 뺴고 동일하게 해주어야하기 때문에
        //cs에 있는 스타일들 가져와 적용시킬 것이다.
        CharShape bold_cs = doc.addNewCharShape();
        CharShape cs = doc.getCharShapeList().get(6);
        
		//bold_cs의 FaceNameId를 설정하기위해 cs의 FaceNameIds를 조회하니 총 7개의 언어에 대해 똑같이 1의 id를 가졌다.
        //따라서 bold_cs의 모든 faceNameId를 setForAll로 한번에 설정할 것이고 그 값은 cs의 FaceNameId의 첫번째 인덱스의 값을 가져올것이다.
        //(모든 인덱스/언어가 똑같은 id를 가졌기 때문에 첫번째(0)으로 지정했다.
        int faceNameId = cs.getFaceNameIds().getArray()[0];
        bold_cs.getFaceNameIds().setForAll(faceNameId);
		
        //cs.getRatios().getArrary()를 하면 언어별 글자 장평의 값이 저장된 배열을 반환한다.
        //길이는 7이다. 한글, 한자, 영어, 일본어, 기호, 사용자정의, 기타 언어로 총 7개 언어에 대해 정의하기 때문이다.
        //보통 이를 정의할때 .getRatios().setForAll(short x)로 일괄적인 short값 x로 7개의 언어를 함께 정하므로
        //getArray()에 있는 어떤 값이든 같은 값을 갖는다.(아닐 수도 있지만 보통 그렇다)
        //그래서 우리는 cs의 한글장평의 short 값을 사용할것이다.
        bold_cs.getRatios().setForAll(cs.getRatios().getHangul());
        
        //같은 원리로 이전의 설정(cs)가 7개의 언어에 대해 같은 값을 가져서 한글에 대한 값으로 일괄적으로 설정해준다.
        bold_cs.getCharSpaces().setForAll(cs.getCharSpaces().getHangul());
        bold_cs.getRelativeSizes().setForAll(cs.getRelativeSizes().getHangul());
        bold_cs.getCharOffsets().setForAll(cs.getCharOffsets().getHangul());
        
        //cs의 기준크기와 동일하게 적용시킨다.
        bold_cs.setBaseSize(cs.getBaseSize());
        //기울임체가 아니므로 false
        bold_cs.getProperty().setItalic(false);
        //볼드체를 적용시켜야하므로 true
        bold_cs.getProperty().setBold(true);
        //cs와 같은 값을 설정해준다.
        bold_cs.getProperty().setUnderLineSort(cs.getProperty().getUnderLineSort());
        bold_cs.getProperty().setOutterLineSort(cs.getProperty().getOutterLineSort());
        bold_cs.getProperty().setShadowSort(cs.getProperty().getShadowSort());
        bold_cs.getProperty().setEmboss(cs.getProperty().isEmboss());
        bold_cs.getProperty().setEngrave(cs.getProperty().isEngrave());
        bold_cs.getProperty().setSuperScript(cs.getProperty().isSuperScript());
        bold_cs.getProperty().setSubScript(cs.getProperty().isSubScript());
        bold_cs.getProperty().setStrikeLine(cs.getProperty().isStrikeLine());
        bold_cs.getProperty().setEmphasisSort(cs.getProperty().getEmphasisSort());
        bold_cs.getProperty().setUsingSpaceAppropriateForFont(cs.getProperty().isUsingSpaceAppropriateForFont());
        bold_cs.getProperty().setStrikeLineShape(cs.getProperty().getStrikeLineShape());
        bold_cs.getProperty().setKerning(cs.getProperty().isKerning());
        bold_cs.setShadowGap1(cs.getShadowGap1());
        bold_cs.setShadowGap2(cs.getShadowGap2());
        bold_cs.getCharColor().setValue(cs.getCharColor().getValue());
        bold_cs.getUnderLineColor().setValue(cs.getUnderLineColor().getValue());
        bold_cs.getShadeColor().setValue(cs.getShadeColor().getValue());
        bold_cs.getShadowColor().setValue(cs.getShadowColor().getValue());
        bold_cs.setBorderFillId(cs.getBorderFillId());
		
        return doc.getCharShapeList().size() - 1;
	}
	private static void setNewParagraph(Paragraph firstParagraph, String str, Paragraph prePara,boolean bold) {
		int x = prePara.getHeader().getParaShapeId();
		setParaHeader(firstParagraph, x);
        setParaText(firstParagraph, str);
        setParaCharShape(firstParagraph, bold);
        setParaLineSeg(firstParagraph);
	}

	private static void setParaLineSeg(Paragraph firstParagraph) {
		firstParagraph.createLineSeg();
        ParaLineSeg pls = firstParagraph.getLineSeg();
        LineSegItem lsi = pls.addNewLineSegItem();

        lsi.setTextStartPositon(0);
        lsi.setLineVerticalPosition(0);
        lsi.setLineHeight(ptToLineHeight(10.0));
        lsi.setTextPartHeight(ptToLineHeight(10.0));
        lsi.setDistanceBaseLineToLineVerticalPosition(ptToLineHeight(10.0 * 0.85));
        lsi.setLineSpace(ptToLineHeight(3.0));
        lsi.setStartPositionFromColumn(0);
        lsi.setSegmentWidth((int) mmToHwp(50.0));
        lsi.getTag().setFirstSegmentAtLine(true);
        lsi.getTag().setLastSegmentAtLine(true);
	}

	private static void setParaCharShape(Paragraph firstParagraph,boolean bold) {
		firstParagraph.createCharShape();
        ParaCharShape pcs = firstParagraph.getCharShape();
        // 셀의 글자 모양을 이미 만들어진 글자 모양으로 사용함
        // 2번이 함초롱바탕.
        pcs.addParaCharShape(0, 6);
        //볼드체로 바꾸기 적용중
//        if(bold== true) {
//        	pcs.addParaCharShape(0, 7);
//        }else {
//        	pcs.addParaCharShape(0, 6);
//        }
	}

	private static void setParaText(Paragraph firstParagraph, String tmp) {
		firstParagraph.createText();
        ParaText pt = firstParagraph.getText();
        try {
            pt.addString(tmp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
	}

	private static void setParaHeader(Paragraph firstParagraph, int ParaShapeId) {
		ParaHeader ph = firstParagraph.getHeader();
		ph.setLastInList(true);
		ph.setParaShapeId(ParaShapeId);
		ph.setStyleId((short) 0);
		ph.getDivideSort().setDivideSection(false);
        ph.getDivideSort().setDivideMultiColumn(false);
        ph.getDivideSort().setDividePage(false);
        ph.getDivideSort().setDivideColumn(false);
        ph.setCharShapeCount(1);
        ph.setRangeTagCount(0);
        ph.setLineAlignCount(1);
        ph.setInstanceID(0);
        ph.setIsMergedByTrack(0);	
	}

	private static long mmToHwp(double mm) {
		return (long) (mm * 72000.0f / 254.0f + 0.5f);
	}

	private static int ptToLineHeight(double pt) {
		return (int) (pt * 100.0f);
	}
	
}