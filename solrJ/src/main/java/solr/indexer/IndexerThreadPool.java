package solr.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;

import excel.RicettaDocument;

public class IndexerThreadPool {

	private List<Thread> threads;
	private Iterator<RicettaDocument> iter;
	private SolrClient client;
	private String collection;
	private int timeOut;

	public IndexerThreadPool(List<RicettaDocument> ricette, int nThread, SolrClient client, String collection,
			int timeOut) {
		iter = ricette.iterator();
		this.client = client;
		this.collection = collection;
		this.timeOut = timeOut;
		threads = new ArrayList<>();
		for(int i=0;i<nThread;i++) {
			threads.add(new Thread(new IndexerThread("Thread-"+i)));
		}
	}
	
	public void run() throws InterruptedException {
		for(Thread t: threads) {
			t.start();
		}
		for(Thread t: threads) {
			t.join();
		}
	}

	private class IndexerThread implements Runnable {

		String name;

		public IndexerThread(String name) {
			this.name = name;
		}

		public void run() {
			RicettaDocument ricetta = null;
			while ((ricetta = getDoc()) != null) {
				System.out.println(name+"----"+ricetta);
				if (!ricetta.asNull()) {
					try {
						UpdateResponse response = client.addBean(collection, ricetta, timeOut);
						System.out.println(name+"----"+response);
						
						
					} catch (Exception e) {
						System.err.println(name+"----"+e.getMessage() + ": " + ricetta);
					}
				} else {
					System.err.println(name+"----"+"alcuni campi null!!! da capire come gestire bene questi schemi");
				}
			}
			synchronized (collection) {
				try {
					client.commit(collection);
				} catch (SolrServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized RicettaDocument getDoc() {
		if (iter.hasNext())
			return iter.next();
		else
			return null;
	}

}
