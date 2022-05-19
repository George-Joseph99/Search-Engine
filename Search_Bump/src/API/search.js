import axios from "./index";

export const getSearchResults = async (queryParameters) => {
  return (
    await axios.get(`/search`, {
      params: queryParameters,
    })
  ).data;
};
