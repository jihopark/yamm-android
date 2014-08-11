package com.teamyamm.yamm.app;

import java.util.List;

/**
 * Created by parkjiho on 7/16/14.
 *
 * Certain activity implements FriendListInterface to use FriendsFragment
 */
public interface FriendListInterface {
    public final static int YAMM = 1;
    public final static int CONTACT = 2;

    public List<YammItem> getList(int type);

    public void setConfirmButtonEnabled(boolean b, int type);

    public String getFragmentTag(int type);
}
