package com.bergerkiller.bukkit.coasters.editor.history;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.bergerkiller.bukkit.coasters.tracks.TrackLockedException;
import com.bergerkiller.bukkit.coasters.world.CoasterWorld;

/**
 * Single point in history which can be reverted or re-done.
 */
public abstract class HistoryChange extends HistoryChangeCollection {
    private final List<HistoryChange> children = new LinkedList<HistoryChange>();
    private final long timestamp;
    protected final CoasterWorld world;

    public HistoryChange(CoasterWorld world) {
        this.world = world;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * To be implemented to perform the changes
     * 
     * @param undo whether undoing instead of redoing
     * @throws TrackLockedException if a coaster is being modified that is locked
     */
    protected abstract void run(boolean undo) throws TrackLockedException;

    /**
     * Performs the history change.
     * Does a best-effort in the event of locked coasters.
     * 
     * @throws TrackLockedException if some changes failed to be made because a coaster is locked
     */
    public final void redo() throws TrackLockedException {
        boolean someTracksLocked = false;
        try {
            this.run(false);
        } catch (TrackLockedException ex) {
            someTracksLocked = true;
        }
        for (HistoryChange child : this.children) {
            try {
                child.redo();
            } catch (TrackLockedException ex) {
                someTracksLocked = true;
            }
        }
        if (someTracksLocked) {
            throw new TrackLockedException();
        }
    }

    /**
     * Undoes the history change.
     * Does a best-effort in the event of locked coasters.
     * 
     * @throws TrackLockedException if some changes failed to be made because a coaster is locked
     */
    public final void undo() throws TrackLockedException {
        boolean someTracksLocked = false;
        ListIterator<HistoryChange> iter = this.children.listIterator(this.children.size());
        while (iter.hasPrevious()) {
            try {
                iter.previous().undo();
            } catch (TrackLockedException ex) {
                someTracksLocked = true;
            }
        }
        try {
            this.run(true);
        } catch (TrackLockedException ex) {
            someTracksLocked = true;
        }
        if (someTracksLocked) {
            throw new TrackLockedException();
        }
    }

    /**
     * Adds a child change to be performed after this change is performed ({@link #redo()}).
     * When undoing, the child changes are performed in reverse order beforehand.
     * 
     * @param childChange to add
     * @return childChange added
     */
    @Override
    public HistoryChange addChange(HistoryChange childChange) {
        this.children.add(childChange);
        return childChange;
    }

    @Override
    public void removeChange(HistoryChange childChange) {
        this.children.remove(childChange);
    }

    @Override
    public boolean hasChanges() {
        return !this.children.isEmpty();
    }

    /**
     * Gets whether this historic change is a no-op: It has no effect when undoing
     * or redoing it.
     *
     * @return True if this is a No-Op
     */
    public boolean isNOOP() {
        return false;
    }

    /**
     * Gets a milliseconds timestamp of when this history change was created
     *
     * @return timestamp of the change
     * @see System#currentTimeMillis()
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets how many seconds ago {@link #getTimestamp()} was
     *
     * @return seconds ago this change was
     */
    public int getSecondsAgo() {
        return (int) ((System.currentTimeMillis() - timestamp) / 1000L);
    }
}
