export const saveSearchState = (searchQuery, searchResults) => {
  localStorage.setItem("last-searched-query", searchQuery);
  localStorage.setItem("search-results", JSON.stringify(searchResults));
};

export const retrieveSearchResults = () => {
  return JSON.parse(localStorage.getItem("search-results") || "[]");
};

export const retrieveLastSearchedQuery = () => {
  return localStorage.getItem("last-searched-query");
};
