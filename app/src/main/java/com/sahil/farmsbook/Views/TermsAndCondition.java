package com.sahil.farmsbook.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

import com.sahil.farmsbook.MainActivity;
import com.sahil.farmsbook.R;

public class TermsAndCondition extends AppCompatActivity implements OnPageChangeListener,OnLoadCompleteListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SAMPLE_FILE = "terms1.pdf";
    public static final String SELLER_TERMS_FILE = "sellerterms.pdf";

    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(TermsAndCondition.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Terms and Condition");
        pdfView= (PDFView)findViewById(R.id.pdfView);

        String type = getIntent().getStringExtra("type");
        if(type.equals("Login")){
            displayFromAsset(SAMPLE_FILE);

        }else{
            displayFromAsset(SELLER_TERMS_FILE);

        }

    }
    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromAsset(pdfFileName)
                .defaultPage(pageNumber)
                .enableSwipe(true)

                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }


    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }



}
