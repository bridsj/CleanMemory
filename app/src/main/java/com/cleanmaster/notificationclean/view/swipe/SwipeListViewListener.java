/*
 * Copyright (C) 2013 47 Degrees, LLC
 * http://47deg.com
 * hello@47deg.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cleanmaster.notificationclean.view.swipe;

/**
 * Listener to get callback notifications for the SwipeListView
 */
public interface SwipeListViewListener {

    /**
     * Called when open animation finishes
     * @param position list item
     * @param toRight Open to right
     */
    void onOpened(int position, boolean toRight);

    /**
     * Called when close animation finishes
     * @param position list item
     * @param fromRight Close from right
     */
    void onClosed(int position, boolean fromRight);

    /**
     * Called when the list changed
     */
    void onListChanged();

    /**
     * Called when user is moving an item
     * @param position list item
     * @param x Current position X
     */
    void onMove(int position, float x);

    /**
     * Called when user clicks on the front view
     * @param position list item
     */
    void onClickFrontView(int position);

    /**
     * Called when user clicks on the back view
     * @param position list item
     */
    void onClickBackView(int position);

    /**
     * Called when user dismisses items
     * @param isManual
     * @param reverseSortedPositions Items dismissed
     */
    void onDismiss(boolean isManual, int[] reverseSortedPositions);

}
