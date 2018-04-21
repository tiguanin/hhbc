package recognizition;

import java.util.HashMap;

/**
 * Координаты распознанного лица.
 */
public class FaceRectangle {
    private int height;
    private int left;
    private int top;
    private int width;
    private String imageUrl; // здесь это лишнее

    public FaceRectangle(int height, int left, int top, int width, String imageUrl) {
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
        this.imageUrl = imageUrl;
    }

    public FaceRectangle(HashMap<String, Object> params, String imageUrl) {
        this.height = (Integer) params.get("height");
        this.left = (Integer) params.get("left");
        this.top = (Integer) params.get("top");
        this.width = (Integer) params.get("width");
        this.imageUrl = imageUrl;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
