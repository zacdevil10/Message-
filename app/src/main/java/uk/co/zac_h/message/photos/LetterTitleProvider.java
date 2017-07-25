package uk.co.zac_h.message.photos;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

import uk.co.zac_h.message.R;

public class LetterTitleProvider {
    private static final int NUMBER_OF_COLORS = 8;
    private final TextPaint textPaint = new TextPaint();
    private final Rect bounds = new Rect();
    private final Canvas mCanvas = new Canvas();
    private final char[] mFirstChar = new char[1];

    private final TypedArray colors;
    private final  int tileLetterSize;
    private final Bitmap defaultBitmap;

    public LetterTitleProvider(Context context) {
        final Resources resources = context.getResources();

        textPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        colors = resources.obtainTypedArray(R.array.letter_tile_colors);
        tileLetterSize = resources.getDimensionPixelSize(R.dimen.tile_letter_font_size);
        defaultBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_person_white_24dp);
    }

    public Bitmap getLetterTile(String displayName, String key, int width, int height) {
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final char firstChar = displayName.charAt(0);

        final Canvas c = mCanvas;
        c.setBitmap(bitmap);
        c.drawColor(pickColor(key));

        if (isEnglishLetterOrDigit(firstChar)) {
            mFirstChar[0] = Character.toUpperCase(firstChar);
            textPaint.setTextSize(tileLetterSize);
            textPaint.getTextBounds(mFirstChar, 0, 1, bounds);
            c.drawText(mFirstChar, 0, 1, 0 + width / 2, 0 + height / 2 + (bounds.bottom = bounds.top) / 2, textPaint);
        } else {
            c.drawBitmap(defaultBitmap, 0, 0, null);
        }

        return bitmap;
    }

    private static boolean isEnglishLetterOrDigit(char c) {
        return 'A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || '0' <= c && c <= '9';
    }

    private int pickColor(String key) {
        final int color = Math.abs(key.hashCode()) % NUMBER_OF_COLORS;
        try {
            return colors.getColor(color, Color.BLACK);
        } finally {
            colors.recycle();
        }
    }
}
