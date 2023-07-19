package odroid.hardkernel.com.BarcodeScanner;

import android.util.Log;

import com.hardkernel.odroid.things.contrib.BarcodeScanner.BarcodeScanner;

import java.util.ArrayList;
import java.util.List;

import odroid.hardkernel.com.BarcodeScanner.openAPI.Google.VolumeInfo;
import odroid.hardkernel.com.BarcodeScanner.openAPI.Google.VolumesSearchRepository;
import odroid.hardkernel.com.BarcodeScanner.printer.LabelLayoutGenerator;
import odroid.hardkernel.com.BarcodeScanner.printer.LayoutItem;
import odroid.hardkernel.com.BarcodeScanner.printer.ZPLScriptor;

public class GoogleBarcodeListener implements BarcodeScanner.BarcodeListener {
    Printer printer;
    AddItemCallback callback;

    public GoogleBarcodeListener(Printer printer, AddItemCallback callback) {
        this.printer = printer;
        this.callback = callback;
    }

    @Override
    public void getBarcode(String barcodes) {
        VolumesSearchRepository.getInstance()
                .getVolumeInfo(barcodes,
                        new VolumesSearchRepository.VolumeInfoResponseListener() {
                            @Override
                            public void onSuccessResponse(VolumeInfo volume) {
                                printer.print(buildScript(volume));
                                callback.addItemArrayList(volume.getImageLinks().getThumbnail(),
                                        volume.getTitle(),volume.getDescription());
                            }

                            @Override
                            public void onFailResponse() {
                                Log.d("VolumeInfo", "FAILED!!!!!");
                            }
                        });
    }
    String buildScript(VolumeInfo volume) {
        ZPLScriptor printer = new ZPLScriptor(new ZPLScriptor.PaperSize(4,6),
                203);

        List<LayoutItem> itemList = new ArrayList<>();

        itemList.add(LayoutItem.builder()
                .type(LayoutItem.Type.Text)
                .options(new String[]{"0"})
                .value(volume.getTitle())
                .build());

        itemList.add(LayoutItem.builder()
                .type(LayoutItem.Type.Text)
                .options(new String[]{"0"})
                .value(volume.getPublishedDate())
                .build());

        itemList.add(LayoutItem.builder()
                .type(LayoutItem.Type.Text)
                .options(new String[]{"0"})
                .value(volume.getDescription())
                .build());

        itemList.add(LayoutItem.builder()
                .type(LayoutItem.Type.ISBN)
                .options(new String[]{"N", "60"})
                .value(volume.getIndustryIdentifiers().get(1).getIdentifier())
                .build());

        itemList.add(LayoutItem.builder().type(LayoutItem.Type.QR)
                .options(new String[]{"N", "2", "6"})
                .value(volume.getInfoLink())
                .build());

        itemList.add(LayoutItem.builder().type(LayoutItem.Type.QR)
                .options(new String[]{"N", "2", "6"})
                .value(volume.getImageLinks().getThumbnail())
                .build());

        LabelLayoutGenerator generator = new LabelLayoutGenerator();
        generator.setOrigin(80, 80);
        generator.setFontSize(20, 25);
        generator.setISBNSizeWidth(3);
        generator.setQRSizeWidth(7);
        printer.addContents(generator.generateLayout(itemList));

        return printer.getPrintScript();
    }
}
