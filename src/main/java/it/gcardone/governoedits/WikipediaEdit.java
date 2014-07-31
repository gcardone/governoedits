package it.gcardone.governoedits;

public class WikipediaEdit {

	private String flag;
	private String page;
	private String pageUrl;
	private String url;
	private int delta;
	private String comment;
	private String wikipedia;
	private String wikipediaUrl;
	private String user;
	private String userUrl;
	private boolean isUnpatrolled;
	private boolean isNewPage;
	private boolean isRobot;
	private boolean isAnonymous;

	public String getFlag() {
		return flag;
	}

	public String getPage() {
		return page;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public String getUrl() {
		return url;
	}

	public int getDelta() {
		return delta;
	}

	public String getComment() {
		return comment;
	}

	public String getWikipedia() {
		return wikipedia;
	}

	public String getWikipediaUrl() {
		return wikipediaUrl;
	}

	public String getUser() {
		return user;
	}

	public String getUserUrl() {
		return userUrl;
	}

	public boolean isUnpatrolled() {
		return isUnpatrolled;
	}

	public boolean isNewPage() {
		return isNewPage;
	}

	public boolean isRobot() {
		return isRobot;
	}

	public boolean isAnonymous() {
		return isAnonymous;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WikipediaEdit [flag=");
		sb.append(flag);
		sb.append(", page=");
		sb.append(page);
		sb.append(", pageUrl=");
		sb.append(pageUrl);
		sb.append(", url=");
		sb.append(url);
		sb.append(", delta=");
		sb.append(delta);
		sb.append(", comment=");
		sb.append(comment);
		sb.append(", wikipedia=");
		sb.append(wikipedia);
		sb.append(", wikipediaUrl=");
		sb.append(wikipediaUrl);
		sb.append(", user=");
		sb.append(user);
		sb.append(", userUrl=");
		sb.append(userUrl);
		sb.append(", isUnpatrolled=");
		sb.append(isUnpatrolled);
		sb.append(", isNewPage=");
		sb.append(isNewPage);
		sb.append(", isRobot=");
		sb.append(isRobot);
		sb.append(", isAnonymous=");
		sb.append(isAnonymous);
		sb.append("]");
		return sb.toString();
	}

	public static class Builder {
		private WikipediaEdit instance = new WikipediaEdit();

		public Builder setFlag(String flag) {
			this.instance.flag = flag;
			return this;
		}

		public Builder setPage(String page) {
			this.instance.page = page;
			return this;
		}

		public Builder setPageUrl(String pageUrl) {
			this.instance.pageUrl = pageUrl;
			return this;
		}

		public Builder setUrl(String url) {
			this.instance.url = url;
			return this;
		}

		public Builder setDelta(int delta) {
			this.instance.delta = delta;
			return this;
		}

		public Builder setComment(String comment) {
			this.instance.comment = comment;
			return this;
		}

		public Builder setWikipedia(String wikipedia) {
			this.instance.wikipedia = wikipedia;
			return this;
		}

		public Builder setWikipediaUrl(String wikipediaUrl) {
			this.instance.wikipediaUrl = wikipediaUrl;
			return this;
		}

		public Builder setUser(String user) {
			this.instance.user = user;
			return this;
		}

		public Builder setUserUrl(String userUrl) {
			this.instance.userUrl = userUrl;
			return this;
		}

		public Builder setUnpatrolled(boolean isUnpatrolled) {
			this.instance.isUnpatrolled = isUnpatrolled;
			return this;
		}

		public Builder setNewPage(boolean isNewPage) {
			this.instance.isNewPage = isNewPage;
			return this;
		}

		public Builder setRobot(boolean isRobot) {
			this.instance.isRobot = isRobot;
			return this;
		}

		public Builder setAnonymous(boolean isAnonymous) {
			this.instance.isAnonymous = isAnonymous;
			return this;
		}

		public WikipediaEdit build() {
			return instance;
		}
	}
}
