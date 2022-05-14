package main;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import DB.MongoDB;



public class Webcrawler {
	private String startUrl;
	
	MongoDB database;
    private static final int MAX_TO_BE_CRAWLED = 500;
   // private static final int MAX_TO_BE_RECRAWLED = 10;
    private ConcurrentHashMap <String, Boolean> isVisited;
    private ArrayBlockingQueue <String> toVisit;
    
    
    public int getNumberofVisitedPages() {
        return this.isVisited.size();
    }
    public int getNumberofPagesToVisit() {
        return this.toVisit.size();
    }
	
	public Webcrawler(String startUrl) {
		this.startUrl =startUrl;
		
	}
	public boolean checkStop() {
		if ( this.isVisited.size() + this.toVisit.size() >= MAX_TO_BE_CRAWLED) {
			return true;
		}
		else return false;
	}
	public Webcrawler(ArrayList <String> toVisit, ArrayList<String> visited,MongoDB database) {
		
		this.isVisited = new ConcurrentHashMap<String, Boolean>();
		if(visited != null)
        {
            for (String url : visited) {
                this.isVisited.put(url, true);
            }
        }
		this.toVisit = new ArrayBlockingQueue <String> (5000);
		if(toVisit != null)
        {
            for (String url : toVisit) 
                this.toVisit.offer(url); //add() method throws error when queue is full 
            //offer() method returns false in such situation.
        }
		this.database = database;
		
	}
	public void start() throws MalformedURLException {
		
		//crawl(this.startUrl);
		boolean finished = false;
		while(!finished) {
			crawl();
			if ( this.isVisited.size() >= MAX_TO_BE_CRAWLED) {
				finished = true;
			}

		}
		
	}
	private String normalizeLink (String link, String base) {
		
		try {
			URL u = new URL(base);
			if (link.startsWith("./")) {
				link = link.substring(1, link.length());				
				//form the full link
				link = u.getProtocol()+ "://"+ u.getAuthority() + rmvFileFromPath(u.getPath()) +link;
			}
			else if (link.startsWith("#")) {
				link = base +"/"+ link ;
			}
			else if (link.startsWith("javascript")) {
				link = null;
			}
			else if (link.startsWith("/") || link.startsWith("../") || (!link.startsWith("http://") && !link.startsWith("https://")) ) {
				
				link = u.getProtocol()+ "://"+ u.getAuthority() + rmvFileFromPath(u.getPath())+link;
			}
			link = link.toLowerCase();
			return link;
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public boolean crawl (String url) throws MalformedURLException{
		    
		String html= getHTML(url);
		
		Document doc = Jsoup.parse(html);

		Elements elements = doc.select("a");
		
		for (Element e: elements) {
			String href = e.attr("href");
			href = normalizeLink(href,url);
			//System.out.println(href);
		}
		
		return true;
	}
	//crawl function gets links in a url
	public  boolean crawl () throws MalformedURLException{
		//System.out.println("-----------------"+Thread.currentThread().getName()+"starting crawl function------------------:\n"
				//+ " with toVisit size "+toVisit.size()+" visitedSize " + isVisited.size());
		// -->  check if max number of urls is reached
		synchronized(toVisit) {
			if ( this.isVisited.size() >= MAX_TO_BE_CRAWLED) 
			return true;
		}
		
		String url = this.toVisit.poll();
		if (url == null) 
			return false; 
		//System.out.println("-----------------URL PROCESSING NOW------------------:");
		//System.out.println(url);
		//System.out.println("------------------------------------------------------:");
		
		
		//1  -->  check if robot is allowed
		String robotFileContent = getRobotFile(url);
		boolean isRobotAllowed = isRobotAllowed(robotFileContent,url);
		if (!isRobotAllowed) {
			System.out.println("-----------------ROBOT NOT ALLOWED------------------:");
			return false;
		}
			

		
		//2  -->  check if url is visited before 
		if (this.isVisited.containsKey(url)) {
			System.out.println("-----------------VISITED BEFORE------------------:"); 
			return false ;
		}; 
		
        //Now url is valid for crawling so add to visited list
		synchronized(this.isVisited) {
        this.isVisited.put(url, true);
        }
        // get html 
		String html= getHTML(url);
		if (html=="") return false;
		Document doc = Jsoup.parse(html);
		
		// insert page and its content in db
		synchronized(this.database) {
		if(database.getURL(url).size()==0)
			database.InsertUrl(url, html);
		else 
			database.setContent(url,html);
		
	    database.setCrawled(url);
		}
	    //Now get all links in this url
		Elements elements = doc.select("a");
		 System.out.println("Thread " + Thread.currentThread().getName() + " visited page: "
	                + url + " \nFound (" + elements.size() + ") link(s)");
		for (Element e: elements) {
			String href = e.attr("href");
			href = normalizeLink(href,url);
			//System.out.println(href);
			if (href ==null) continue;
			//TODO: add to database and toVisit array
			synchronized(this.toVisit) {
				 if(!this.toVisit.contains(href) && !this.isVisited.containsKey(href)) {
					 synchronized(this.database) {	if(database.getURL(href).size()==0) database.InsertUrl(href);}
					   this.toVisit.add(href);
				 }
			 }
		}
		
		return false ;
	}
//https://en.wikipedia.org/wiki//wiki/Bruce_Springsteen	
	private String getHTML(String url) {
		URL u;
		try {
			u= new URL(url);
			URLConnection connection = u.openConnection();
			connection.setRequestProperty("User-Agent", "BBot/1.0");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			InputStream is = connection.getInputStream();
			BufferedReader reader =new BufferedReader(new InputStreamReader(is));
			
			String line;
			String html= "";
			while ((line = reader.readLine())!=null) {
				html += line +"\n";
			}
			html = html.trim();
			return html;

		}catch(Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}
	private String rmvFileFromPath(String path) {
		int pos = path.lastIndexOf("/");
		return pos<=-1? path:path.substring(0,pos+1);
	}
	
	
    public String getRobotFile(String url) throws MalformedURLException {
    	
    	URL u= new URL(url);

        String robotFile = u.getProtocol() + "://" + u.getHost() + "/robots.txt";
        URL robotUrl;
        try { 
        	robotUrl = new URL(robotFile);
        } catch (MalformedURLException e) {
            return null;
        }

        String robotFileContent = "";
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(robotUrl.openStream()));
            String fileLine;
            while ((fileLine = in.readLine()) != null)
            {
        		
            	robotFileContent += fileLine;
            	robotFileContent += "\n";
            }
            in.close();
            return robotFileContent==""? null: robotFileContent ;

        }
        catch (IOException e)//robots.txt not found
        {
            return null; 
        }
    }
    public boolean isRobotAllowed (String robotFileContent, String url ) throws MalformedURLException {
    	
    	if (robotFileContent== null) return false;
        if (robotFileContent.contains("Disallow:"))
        {
        	String userAgent = null;
            String[] robotFileLines = robotFileContent.split("\n");
            ArrayList <RobotRule> robotRules = new ArrayList<>();
            
            for (int i = 0; i < robotFileLines.length; i++)
            {
                String line = robotFileLines[i].trim();
                if (line.toLowerCase().startsWith("user-agent"))
                {
                    int from = line.indexOf(":") + 1;
                    int till   = line.length();
                    userAgent = line.substring(from, till).trim().toLowerCase();
                }
                else if (line.startsWith("Disallow:")) {
                    if (userAgent != null) {
                        int from = line.indexOf(":") + 1;
                        int till   = line.length();
                        RobotRule r = new RobotRule();
                        r.userAgent = userAgent;
                        r.rule = line.substring(from, till).trim();
                        robotRules.add(r);
                    }
                }
            }

            for (RobotRule robotRule : robotRules)
            {
                if (robotRule.rule.length() == 0) continue;         // allows all
                // disallow when user agent is googlebot or *
                if (robotRule.userAgent.equals("googlebot") || robotRule.userAgent.equals("*")) {
                    if (robotRule.rule.equals("/"))
                        return false; // disallows all
                	URL u= new URL(url);
                    String path = u.getPath();
                    if (path.length() >= robotRule.rule.length()) 
                    {
                        String ruleCompare = path.substring(0, robotRule.rule.length());
                        if (ruleCompare.equals(robotRule.rule))
                            return false;
                    }
                }
            }
        }
        return true; //file doesn't contain any Disallow
    }
    
    
    
    class RobotRule {
        public String userAgent;
        public String rule;
    }
	
}

class webCrawlerRunnable implements Runnable {
    private Webcrawler webCrawler;

    public webCrawlerRunnable (Webcrawler webCrawler) {
        this.webCrawler = webCrawler;
    }

    public void run () {
    	
    	//System.out.println("this is thread" +Thread.currentThread().getName());
    	boolean stop = false;
		while(!stop) {
			try {
				//System.out.println("this is thread" +Thread.currentThread().getName());
				stop = webCrawler.crawl();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			//stop=webCrawler.checkStop();
		}
		System.out.println("this is thread" +Thread.currentThread().getName()+ " Exiting with " +webCrawler.getNumberofVisitedPages());

    }
}
	
