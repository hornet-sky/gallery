package com.example.paginggallery;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class LoadPhotosResult {
    private int total;
    private int totalHits;
    private List<PhotoItem> hits;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public List<PhotoItem> getHits() {
        return hits;
    }

    public void setHits(List<PhotoItem> hits) {
        this.hits = hits;
    }

    static class PhotoItem implements Parcelable {
        private int id;
        private String webformatURL;
        private int webformatWidth;
        private int webformatHeight;
        private String largeImageURL;
        private int likes;
        private int favorites;
        private int downloads;
        private String user;

        protected PhotoItem(Parcel in) {
            id = in.readInt();
            webformatURL = in.readString();
            webformatWidth = in.readInt();
            webformatHeight = in.readInt();
            largeImageURL = in.readString();
            likes = in.readInt();
            favorites = in.readInt();
            downloads = in.readInt();
            user = in.readString();
        }

        public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
            @Override
            public PhotoItem createFromParcel(Parcel in) {
                return new PhotoItem(in);
            }

            @Override
            public PhotoItem[] newArray(int size) {
                return new PhotoItem[size];
            }
        };

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getWebformatURL() {
            return webformatURL;
        }

        public void setWebformatURL(String webformatURL) {
            this.webformatURL = webformatURL;
        }

        public String getLargeImageURL() {
            return largeImageURL;
        }

        public void setLargeImageURL(String largeImageURL) {
            this.largeImageURL = largeImageURL;
        }

        public int getWebformatWidth() {
            return webformatWidth;
        }

        public void setWebformatWidth(int webformatWidth) {
            this.webformatWidth = webformatWidth;
        }

        public int getWebformatHeight() {
            return webformatHeight;
        }

        public void setWebformatHeight(int webformatHeight) {
            this.webformatHeight = webformatHeight;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public int getFavorites() {
            return favorites;
        }

        public void setFavorites(int favorites) {
            this.favorites = favorites;
        }

        public int getDownloads() {
            return downloads;
        }

        public void setDownloads(int downloads) {
            this.downloads = downloads;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PhotoItem photoItem = (PhotoItem) o;

            if (id != photoItem.id) return false;
            if (webformatWidth != photoItem.webformatWidth) return false;
            if (webformatHeight != photoItem.webformatHeight) return false;
            if (likes != photoItem.likes) return false;
            if (favorites != photoItem.favorites) return false;
            if (downloads != photoItem.downloads) return false;
            if (webformatURL != null ? !webformatURL.equals(photoItem.webformatURL) : photoItem.webformatURL != null)
                return false;
            if (largeImageURL != null ? !largeImageURL.equals(photoItem.largeImageURL) : photoItem.largeImageURL != null)
                return false;
            return user != null ? user.equals(photoItem.user) : photoItem.user == null;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (webformatURL != null ? webformatURL.hashCode() : 0);
            result = 31 * result + webformatWidth;
            result = 31 * result + webformatHeight;
            result = 31 * result + (largeImageURL != null ? largeImageURL.hashCode() : 0);
            result = 31 * result + likes;
            result = 31 * result + favorites;
            result = 31 * result + downloads;
            result = 31 * result + (user != null ? user.hashCode() : 0);
            return result;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(id);
            parcel.writeString(webformatURL);
            parcel.writeInt(webformatWidth);
            parcel.writeInt(webformatHeight);
            parcel.writeString(largeImageURL);
            parcel.writeInt(likes);
            parcel.writeInt(favorites);
            parcel.writeInt(downloads);
            parcel.writeString(user);
        }
    }
}
