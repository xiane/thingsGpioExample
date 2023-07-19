package odroid.hardkernel.com.BarcodeScanner;

import android.util.Log;

import com.hardkernel.odroid.things.contrib.BarcodeScanner.BarcodeScanner;

import java.util.ArrayList;
import java.util.List;

import odroid.hardkernel.com.BarcodeScanner.openAPI.Kakao.BookInfo;
import odroid.hardkernel.com.BarcodeScanner.openAPI.Kakao.BookSearchRepository;
import odroid.hardkernel.com.BarcodeScanner.openAPI.Kakao.Document;
import odroid.hardkernel.com.BarcodeScanner.printer.LabelLayoutGenerator;
import odroid.hardkernel.com.BarcodeScanner.printer.LayoutItem;
import odroid.hardkernel.com.BarcodeScanner.printer.ZPLScriptor;

public class KakaoBarcodeListener implements BarcodeScanner.BarcodeListener {
    Printer printer;
    AddItemCallback callback;

    public KakaoBarcodeListener(Printer printer, AddItemCallback callback) {
        this.printer = printer;
        this.callback = callback;
    }
    @Override
    public void getBarcode(String barcodes) {
        BookSearchRepository.getInstance()
                .getBookInfo(barcodes, 1, 10,
                        new BookSearchRepository.BookResponseListener() {
                            @Override
                            public void onSuccessResponse(BookInfo bookInfo) {
                                Document book = bookInfo.getDocuments().get(0);
                                printer.print(buildScript(book));
                                callback.addItemArrayList(book.getThumbnail(),
                                                book.getTitle(), book.getContents());
                            }

                            @Override
                            public void onFailResponse() {
                                Log.d("BookINFO", "FAILED!!!!");
                            }
                        });
    }

    String buildScript(Document book) {
        ZPLScriptor printer = new ZPLScriptor(new ZPLScriptor.PaperSize(4,6),
                203);

        List<LayoutItem> itemList = new ArrayList<>();

        itemList.add(LayoutItem.builder()
                .type(LayoutItem.Type.Text)
                .options(new String[]{"0"})
                .value(book.getTitle())
                .build());

        itemList.add(LayoutItem.builder()
                .type(LayoutItem.Type.Text)
                .options(new String[]{"0"})
                .value(book.getDatetime())
                .build());
        if (!book.getContents().equals("")) {
            itemList.add(LayoutItem.builder()
                    .type(LayoutItem.Type.Text)
                    .options(new String[]{"0"})
                    .value(book.getContents())
                    .build());
        }

        itemList.add(LayoutItem.builder()
                .type(LayoutItem.Type.ISBN)
                .options(new String[]{"N", "60"})
                .value(book.getIsbn())
                .build());

        itemList.add(LayoutItem.builder().type(LayoutItem.Type.QR)
                .options(new String[]{"N", "2", "6"})
                .value(book.getUrl())
                .build());

        itemList.add(LayoutItem.builder().type(LayoutItem.Type.QR)
                .options(new String[]{"N", "2", "6"})
                .value(book.getThumbnail())
                .build());

        LabelLayoutGenerator generator = new LabelLayoutGenerator();
        generator.setOrigin(80, 80);
        generator.setFontSize(20, 25);
        generator.setISBNSizeWidth(3);
        generator.setQRSizeWidth(5);
        printer.addContents(generator.generateLayout(itemList));

        return printer.getPrintScript();
    }
}
