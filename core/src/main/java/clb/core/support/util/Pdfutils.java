package clb.core.support.util;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.*;
import com.itextpdf.awt.geom.Rectangle2D.Float;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gan on 2017/6/21.
 */
public class Pdfutils {

    float[] resu = null;

    List<float[]> resus = new ArrayList<float[]>();

    List<float[]> others = null;

    int i = 0;

    //爬虫程序名
    String spiderName = null;

    //关键字获取时的页数
    int keyWordPageNum = 0;

    //文件存储路径
    String filePath = null;

    //处理后的文件名
    String replaceName = null;



    /**
     * 去除PDF水印，logo
     * @param filename
     * description;目标根路径
     */
    private void extractImage(String filename){

        PdfReader reader = null;
        try {
            //读取pdf文件
            reader = new PdfReader(filename);
            FileOutputStream output = new FileOutputStream("C:\\Users\\hand\\Desktop\\copy.pdf");
            PdfStamper stp = new PdfStamper(reader,output);
            //获得pdf文件的页数
            int sumPage = reader.getNumberOfPages();
            //读取pdf文件中的每一页
            for(int i = 1;i <= 1;i++){
                //得到pdf每一页的字典对象
                PdfDictionary dictionary = reader.getPageN(i);
                //通过RESOURCES得到对应的字典对象
                PdfDictionary res = (PdfDictionary) PdfReader.getPdfObject(dictionary.get(PdfName.RESOURCES));
//                reader.killIndirect(res);
                //得到XOBJECT图片对象
                PdfDictionary xobj = (PdfDictionary) PdfReader.getPdfObject(res.get(PdfName.XOBJECT));
                for(Iterator it = xobj.getKeys().iterator();it.hasNext();){
                    PdfObject obj = xobj.get((PdfName)it.next());
                    if(obj.isIndirect()){
                        PdfDictionary tg = (PdfDictionary) PdfReader.getPdfObject(obj);
                        try {
                            PdfName type = (PdfName) PdfReader.getPdfObject(tg.get(PdfName.SUBTYPE));
                            if(PdfName.IMAGE.equals(type)){
                                PdfObject object =  reader.getPdfObject(obj);
                                if(object.isStream()){
                                    //删除掉这一层
                                    reader.killIndirect(obj);
                                }
                            }
                        }catch (Exception e){
                            continue;
                        }
                    }
                }
            }
            reader.removeUnusedObjects();
            stp.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //去除PDF关键字,返回新文件路径
    public String handingPdf(String spiderName,String filePath,String replaceName){
        this.spiderName =spiderName;
        this.filePath = filePath;
        this.replaceName =replaceName;
        //初始化关键字数组
        List<String> keyWords = init_keyWords(spiderName);
        //获取关键字的xy绝对位置
        getKeyWords(filePath,keyWords,"main");
        //涂层处理
        String newFileName =  manipulatePdf(filePath,resus);
        return newFileName;
    }


    /**
     * 对特定位置进行涂层
     * @param filename
     * @param xypaths
     */
    private String manipulatePdf(String filename,List<float[]> xypaths){
        PdfReader reader = null;
        String newFileName = filename.replace(spiderName,replaceName);
        try{
            reader = new PdfReader(filename);
            FileOutputStream output = new FileOutputStream(newFileName);
            PdfStamper stp = new PdfStamper(reader,output);
            PdfContentByte canvas = null;
            for(float[] f : xypaths) {
                canvas = stp.getOverContent((int)f[3]);
                canvas.saveState();
                canvas.setColorFill(BaseColor.WHITE);
                canvas.rectangle(f[0], f[1]-2, f[2], 13);
                canvas.fill();
                canvas.restoreState();
            }
            //设置PDF文件不可复制
            stp.setEncryption(null,null, PdfWriter.ALLOW_FILL_IN, false);
            stp.close();
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        File file = new File(filename);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
        return newFileName;
    }

    /**
     *  为了找到PDF中间关键字的坐标
     * @param filePath
     * @param keyWords
     * @return
     */
    private List<float[]> getKeyWords(String filePath,List<String> keyWords,String fromName)
    {

        PdfReader pdfReader =null;
        try {
            pdfReader = new PdfReader(filePath);
            PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(
                    pdfReader);
            if(keyWordPageNum==0){
                keyWordPageNum = pdfReader.getNumberOfPages();
            }
            for (String keyWord : keyWords) {
                // 下标从1开始
                for (i = 1; i <= keyWordPageNum; i++) {
                    pdfReaderContentParser.processContent(i, new RenderListener() {

                        @Override
                        public void renderText(TextRenderInfo textRenderInfo) {
                            String text = textRenderInfo.getText();
                            if (null != text && text.contains(keyWord)) {
                                Float boundingRectange = textRenderInfo.getBaseline().getBoundingRectange();
                                resu = new float[4];
                                resu[0] = boundingRectange.x;
                                resu[1] = boundingRectange.y;
                                resu[2] = boundingRectange.width;
                                System.out.println(text+"_height:"+boundingRectange.height);
                                System.out.println(text+"_getHeight:"+boundingRectange.getHeight());
                                resu[3] = i;
                                if (fromName.equals("main")){
                                    resus.add(resu);
                                }else {
                                    others.add(resu);
                                }
                            }
                        }


                        @Override
                        public void endTextBlock() {

                        }

                        @Override
                        public void renderImage(ImageRenderInfo imageRenderInfo) {

                        }

                        @Override
                        public void beginTextBlock() {
                        }

                    });
                }
            }
        } catch(IOException e)
        {
            e.printStackTrace();
        }finally {
            pdfReader.close();
        }
        return resus;
    }

    /**
     * 初始化路径位置
     * @param spiderName
     */
    private List<String> init_keyWords(String spiderName){
        //去掉关键字的list
        List<String> list = new ArrayList<String>();
        //去掉关键字所在的一行的list
        List<String> otherlist = new ArrayList<String>();
        if (spiderName.equals("aiahk")){
            list.add("建议人:");
            list.add("NG SIU CHUN");
            list.add("电邮地址:");
            list.add("george.ng@hf-group.com.hk");
            list.add("手机号码:");
            list.add(" - NGSIUCHUN");
            list.add("本建议书只可在香港作寿险销售用途。此乃说明文件而非契约。保单条款及条件，请参阅保单契约。");
            list.add("此报价是特别为在香港投保的非香港及非澳门身份证持有人而设。");
            keyWordPageNum = 1;
            otherlist.add("<PGS version");
            handingOthers(otherlist,keyWordPageNum);
        }else if (spiderName.equals("manulife")){
            list.add("僅供內部使用");
            list.add("保險顧問聲明");
            list.add("此呈送保單持有人的說明文件未經任何改動");
            list.add("作出任何與說明文件");

            otherlist.add("LIMITED");
            otherlist.add("FORTUNE FREEDONESS");
            otherlist.add("保單編號");
            otherlist.add("分行");
            otherlist.add("Ver.");
            otherlist.add("宏利人壽保險");
            otherlist.add("於百慕達註冊");
            otherlist.add("AF98");
            otherlist.add("完整銷售說明文件");
            handingOthers(otherlist,keyWordPageNum);
        }else if(spiderName.equals("prudential")){
            otherlist.add("列印日期");
            handingOthers(otherlist,keyWordPageNum);
        }else if(spiderName.equals("metlife")){
            list.add("MetLife Limited ");
            list.add("Private Company");
            list.add("此建議書有效期為");
            list.add("建議書識別碼:");
            list.add("頁: 1/9");
//            list.add("");
//            list.add("");
//            list.add("");
//            list.add("");

            keyWordPageNum = 3;
            otherlist.add("保險經紀:");
            otherlist.add("申請人姓名:");
            otherlist.add("誠意為您提供");
            handingOthers(otherlist,keyWordPageNum);
        }

        return list;
    }

    /**
     * 屏蔽一行（由于有些页面某一行是动态生成的，直接用关键字方法屏蔽不了）
     * add by gan on 17/07/21
     */
    private void handingOthers(List<String> leftString,int pageNum){
        //去除一行只需要定位到y的位置
        float y = 0f;
        float[] path = null;
        try{
            others = new ArrayList<float[]>();
            getKeyWords(filePath,leftString,"others");
            System.out.print(others.size());
            for(float[] f : others) {
                path = new float[4];
                path[0] = 0f;
                path[1] = f[1];
                path[2] = 600;
                path[3] = f[3];
                resus.add(path);
            }
            System.out.print(resus.size());


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void  main(String[] args){
        Pdfutils util = new Pdfutils();
        util.handingPdf("manulife","C:\\Users\\hand\\Desktop\\manulife_20170809224516.pdf","宏利");

    }

//    public static void main(String[] args){
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//        String dateString = sdf.format(new Date());
//        System.out.println(dateString);
//    }

}
