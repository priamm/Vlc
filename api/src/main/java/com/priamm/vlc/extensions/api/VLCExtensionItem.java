package com.priamm.vlc.extensions.api;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class VLCExtensionItem implements Parcelable {

    /**
     * Item type to show it as a directory in VLC browser
     */
    public static final int TYPE_DIRECTORY = 0;
    /**
     * Item type to show it as a video medium in VLC browser
     */
    public static final int TYPE_VIDEO = 1;
     /**
     * Item type to show it as an audio medium in VLC browser
     */
    public static final int TYPE_AUDIO = 2;
    /**
     * Item type to show it as a playlist item in VLC browser
     */
    public static final int TYPE_PLAYLIST = 3;
    /**
     * Item type to show it as a subtitle file in VLC browser
     */
    public static final int TYPE_SUBTITLE = 4;
    /**
     * Unknown type, VLC will guess from its {#link link} or title
     */
    public static final int TYPE_OTHER_FILE = 5;

    public String stringId;

    public String link;
    public String title;
    public String subTitle;

    public Uri imageUri; // for content provider
    public int type; // Using VLC icons. maybe with iconRes?

    /**
     * Simple constructor, with only ids.
     * You have to provide a String or int id for browsable elements (with type #TYPE_DIRECTORY)
     *
     * @param stringId The String to use as an ID, set to #null if you prefer to use the #intId
     */
    public VLCExtensionItem(String stringId) {
        this.stringId = stringId;
    }

    public VLCExtensionItem() {}

    private VLCExtensionItem(Parcel in) {
        readFromParcel(in);
    }

    /**
     * Returns the subTitle of this item. e.g. media artist or album
     */
    public String getSubTitle() {
        return subTitle;
    }

    /**
     * Sets the subTitles string of this item.
     *
     * @param subTitle The subTitle string to set.
     */
    public VLCExtensionItem setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    /**
     * Returns the uri string of the {#link VLCExtensionItem} for playback or download
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the uri String of the {#link VLCExtensionItem}
     *
     * @param link The medium link.
     */
    public VLCExtensionItem setLink(String link) {
        this.link = link;
        return this;
    }

    /**
     * returns the {#link VLCExtensionItem} title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the {#link VLCExtensionItem} title
     *
     * @param title The string to set as title.
     */
    public VLCExtensionItem setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Returns the {#link VLCExtensionItem} icon image link
     */
    public Uri getImageUri() {
        return imageUri;
    }

    /**
     * Sets the uri string of the {#link VLCExtensionItem} icon image.
     *
     * @param imageUri The uri string to set.
     */
    public VLCExtensionItem setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
        return this;
    }

    /**
     * Returns the {#link VLCExtensionItem} type
     * @see {#link setType}.
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type of the {#link VLCExtensionItem}
     *
     * @param type The type among {#link TYPE_DIRECTORY}, {#link TYPE_VIDEO},
     *             {#link TYPE_AUDIO} or {#link TYPE_OTHER_FILE}.
     */
    public VLCExtensionItem setType(int type) {
        this.type = type;
        return this;
    }

    public static final Parcelable.Creator<VLCExtensionItem> CREATOR = new
            Parcelable.Creator<VLCExtensionItem>() {
                public VLCExtensionItem createFromParcel(Parcel in) {
                    return new VLCExtensionItem(in);
                }

                public VLCExtensionItem[] newArray(int size) {
                    return new VLCExtensionItem[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stringId);
        dest.writeString(link);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeParcelable(imageUri, 0);
        dest.writeInt(type);
    }

    public void readFromParcel(Parcel in) {
        stringId = in.readString();
        link = in.readString();
        title = in.readString();
        subTitle = in.readString();
        imageUri = in.readParcelable(null);
        type = in.readInt();
    }
}
