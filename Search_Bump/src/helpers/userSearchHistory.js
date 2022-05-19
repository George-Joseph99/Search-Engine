export const addToSearchHistory = (_id) => {
  let searchHistory = JSON.parse(localStorage.getItem("search-history") || "[]");
  if (searchHistory.findIndex((sent) => sent._id === _id) !== -1) return;
  searchHistory = [...searchHistory, { _id: _id.trim() }];
  localStorage.setItem("search-history", JSON.stringify(searchHistory));
};

export const getSearchHistory = () => {
  const shistory = JSON.parse(localStorage.getItem("search-history") || "[]");
  return shistory.length > 10 ? shistory.slice(0, 10) : shistory;
};

export const clearAllSearchHistory = () => {
  localStorage.removeItem("search-history");
};
export const clearOneSearchHistory = (_id) => {
  let searchHistory = JSON.parse(localStorage.getItem("search-history") || "[]");
  searchHistory = searchHistory.filter((item) => item._id !== _id);
  localStorage.setItem("search-history", JSON.stringify(searchHistory));
  return searchHistory;
};
