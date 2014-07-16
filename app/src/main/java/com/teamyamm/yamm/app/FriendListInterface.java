package com.teamyamm.yamm.app;

import java.util.List;

/**
 * Created by parkjiho on 7/16/14.
 *
 * Certain activity implements FriendListInterface to use FriendsFragment
 */
public interface FriendListInterface {

    public List<YammItem> getList();

    public void setConfirmButtonEnabled(boolean b);

    public String getFragmentTag();
}
