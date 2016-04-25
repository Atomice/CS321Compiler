//JASON HANNAN

package bufmgr;

import diskmgr.DiskMgr;
import global.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <h3>Minibase Buffer Manager</h3>
 * The buffer manager manages an array of main memory pages.  The array is
 * called the buffer pool, each page is called a frame.  
 * It provides the following services:
 * <ol>
 * <li>Pinning and unpinning disk pages to/from frames
 * <li>Allocating and deallocating runs of disk pages and coordinating this with
 * the buffer pool
 * <li>Flushing pages from the buffer pool
 * <li>Getting relevant data
 * </ol>
 * The buffer manager is used by access methods, heap files, and
 * relational operators.
 */
public class BufMgr implements GlobalConst {
  public HashMap<int, FrameDesc> diskMap;
  public ArrayList<FrameDesc> bufferPool;
  public Clock replAlgo;

  /**
   * Constructs a buffer manager by initializing member data.  
   * 
   * @param numframes number of frames in the buffer pool
   */
  public BufMgr(int numframes) {
    for(i = 0; i < numframes; ++i){
      //CREATE PAGEID, BLANK PAGE, SET PINCOUNT TO 0, SET REFERENCE BIT BIT TO 0
      bufferPool.add(new FrameDesc());
    }
    diskMap = new HashMap<int, FrameDesc>();
    replAlgo = new Clock();

    // throw new UnsupportedOperationException("Not implemented");

  } // public BufMgr(int numframes)

  /**
   * The result of this call is that disk page number pageno should reside in
   * a frame in the buffer pool and have an additional pin assigned to it, 
   * and mempage should refer to the contents of that frame. <br><br>
   * 
   * If disk page pageno is already in the buffer pool, this simply increments 
   * the pin count.  Otherwise, this<br> 
   * <pre>
   * 	uses the replacement policy to select a frame to replace
   * 	writes the frame's contents to disk if valid and dirty
   * 	if (contents == PIN_DISKIO)
   * 		read disk page pageno into chosen frame
   * 	else (contents == PIN_MEMCPY)
   * 		copy mempage into chosen frame
   * 	[omitted from the above is maintenance of the frame table and hash map]
   * </pre>		
   * @param pageno identifies the page to pin
   * @param mempage An output parameter referring to the chosen frame.  If
   * contents==PIN_MEMCPY it is also an input parameter which is copied into
   * the chosen frame, see the contents parameter. 
   * @param contents Describes how the contents of the frame are determined.<br>  
   * If PIN_DISKIO, read the page from disk into the frame.<br>  
   * If PIN_MEMCPY, copy mempage into the frame.<br>  
   * If PIN_NOOP, copy nothing into the frame - the frame contents are irrelevant.<br>
   * Note: In the cases of PIN_MEMCPY and PIN_NOOP, disk I/O is avoided.
   * @throws IllegalArgumentException if PIN_MEMCPY and the page is pinned.
   * @throws IllegalStateException if all pages are pinned (i.e. pool is full)
   */
  public void pinPage(PageId pageno, Page mempage, int contents) implements Exception {
    //LOCAL VARIABLES
    FrameDesc cFrame = null;
    FrameDesc checkPage = null;
    boolean checkValid;

    //CHECK TO SEE IF PAGE IS IN POOL
    if(diskMap.containsKey(pageno.pid)) {
      cFrame = diskMap.get(pageno.pid);
      if(cFrame.isPinZero == false && contents == PIN_MEMCPY) {
        throw new IllegalArgumentException("ERROR: PIN_MEMCPY AND PAGE IS PINNED");
      }
      cFrame.increasePinCount();
    }
    else{
      if(getNumFrames() == getNumUnpinned()){
        throw new IllegalStateException("POOL IS FULL");
      }
      index = replAlgo.pickVictim();
      checkPage = bufferPool.get(index);
      checkValid = checkPage.checkFlush();
      if(checkValid) {
        //FLUSH PAGE
        flushPage(checkPage.getDiskPageNumber());
      }

      //New Frame Contents
      if(contents == PIN_DISKIO) {
        //READ DISK PAGE PAGENO INTO CHOSEN FRAME
        //read_page(PageId pageno, Page mempage)
        DiskMgr.read_page(pageno, mempage);
        cFrame = new FrameDesc(pageno, mempage);
        diskMap.entrySet(pageno.pid, cFrame);
        bufferPool.set(index, cFrame);

      }
      else if(contents = PIN_MEMCPY){
        //READ MEMPAGE INTO CHOSEN FRAME
        cFrame = new FrameDesc(pageno, mempage);
        diskMap.entrySet(pageno.pid, cFrame);
        bufferPool.set(index, cFrame);
      }
      else {
        cFrame = new FrameDesc(pageno, new Page());
        diskMap.entrySet(pageno.pid, cFrame);
        bufferPool.set(index, cFrame);
      }
    }
    return;
  }
	//throw new UnsupportedOperationException("Not implemented");

  } // public void pinPage(PageId pageno, Page page, int contents)
  
  /**
   * Unpins a disk page from the buffer pool, decreasing its pin count.
   * 
   * @param pageno identifies the page to unpin
   * @param dirty UNPIN_DIRTY if the page was modified, UNPIN_CLEAN otherwise
   * @throws IllegalArgumentException if the page is not in the buffer pool
   *  or not pinned
   */
  public void unpinPage(PageId pageno, boolean dirty) {

    if(!(diskMap.containsKey(.pid))){
      throw new IllegalArgumentException("Page not in buffer pool");
    }
    FrameDesc cFrame = diskMap.get(pageno.pid);

    if(dirty == UNPIN_DIRTY) {
      cFrame.setDirtyBit(true);
      flushPage(pageno);
    }

    cFrame.reducePinCount();

    //throw new UnsupportedOperationException("Not implemented");

  } // public void unpinPage(PageId pageno, boolean dirty)
  
  /**
   * Allocates a run of new disk pages and pins the first one in the buffer pool.
   * The pin will be made using PIN_MEMCPY.  Watch out for disk page leaks.
   * 
   * @param firstpg input and output: holds the contents of the first allocated page
   * and refers to the frame where it resides
   * @param run_size input: number of pages to allocate
   * @return page id of the first allocated page
   * @throws IllegalArgumentException if firstpg is already pinned
   * @throws IllegalStateException if all pages are pinned (i.e. pool exceeded)
   */
  public PageId newPage(Page firstpg, int run_size) {

    if(getNumFrames() == getNumUnpinned()) {
      throw new IllegalStateException("Pool Exceeded");
    }
    //IF FIRSTPG IS PINNED?

    PageId newPageId = DiskMgr.allocate_page(run_size);

    pinPage(newPageId, firstpg, PIN_MEMCPY);

    return newPageId;
    //throw new UnsupportedOperationException("Not implemented");

  } // public PageId newPage(Page firstpg, int run_size)

  /**
   * Deallocates a single page from disk, freeing it from the pool if needed.
   * 
   * @param pageno identifies the page to remove
   * @throws IllegalArgumentException if the page is pinned
   */
  public void freePage(PageId pageno) {

    if(diskMap.containsKey(pageno.pid)) {
      if(!(diskMap.get(pageno.pid).isPinZero())) {
        throw new IllegalArgumentException("FreePage: Page is Pinned");
      }
      FrameDesc cFrame = diskMap.get(pageno.pid);
      diskMap.remove(pageno.pid);
      if(bufferPool.contains(cFrame)) {
        bufferPool.remove(cFrame);
      }
    }
    DiskMgr.deallocate_page(pageno);
    //throw new UnsupportedOperationException("Not implemented");

  } // public void freePage(PageId firstid)

  /**
   * Write all valid and dirty frames to disk.
   * Note flushing involves only writing, not unpinning or freeing
   * or the like.
   * 
   */
  public void flushAllFrames() {

    for(int i = 0; i < bufferPool.size(); ++i) {
      if (bufferPool.get(i).checkValid()) {
        flushPage(bufferPool.get(i).getPageId());
      }
    }
    //throw new UnsupportedOperationException("Not implemented");

  } // public void flushAllFrames()

  /**
   * Write a page in the buffer pool to disk, if dirty.
   * 
   * @throws IllegalArgumentException if the page is not in the buffer pool
   */
  public void flushPage(PageId pageno) {
    //void write_page(PageId pageno, Page mempage)
	if(!(diskMap.containsKey(pageno.pid))) {
      throw new IllegalArgumentException("No page to flush");
    }
    FrameDesc cFrame = diskMap.get(pageno.pid);
    if(cFrame.isDirtyTrue()) {
      DiskMgr.write_page(pageno, cFrame.getPage());
    }
	//throw new UnsupportedOperationException("Not implemented");
    
  }

   /**
   * Gets the total number of buffer frames.
   */
  public int getNumFrames() {
    return bufferPool.size();
    //throw new UnsupportedOperationException("Not implemented");
  }

  /**
   * Gets the total number of unpinned buffer frames.
   */
  public int getNumUnpinned() {
    int count = 0;
    for(int i = 0; i < bufferPool.size(); ++i) {
      if(bufferPool.get(i).isPinZero()) {
        ++count;
      }
    }
    return count;
    //throw new UnsupportedOperationException("Not implemented");
  }

} // public class BufMgr implements GlobalConst
