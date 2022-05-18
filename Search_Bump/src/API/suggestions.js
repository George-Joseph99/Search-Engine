import axios from "./index";

export const getSuggestions = async (searchQuery) => {
  return (
    await axios.get(`/history`, {
      params: {
        q: searchQuery,
      },
    })
  ).data;
};
