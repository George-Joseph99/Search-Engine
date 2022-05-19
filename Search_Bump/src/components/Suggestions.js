import React from "react";
import { useHistory } from "react-router";
import { clearOneSearchHistory } from "../helpers/userSearchHistory";
import "./Suggestions.scss";

const Suggestions = ({ suggestions, color, styles, marg, width, onDeleteFromHistory, searchInput }) => {
  const history = useHistory();
  //   const inResultsStyle = {
  //     left: "-100px",
  //   };
  return (
    <div className="suggestions-list" style={{ marginTop: marg, width: width }}>
      {suggestions.map((suggestion) => {
        return (
          <div key={suggestion._id} className={styles} style={{ backgroundColor: color }}>
            {searchInput === "" && <i class="fas fa-trash" onClick={() => onDeleteFromHistory(clearOneSearchHistory(suggestion._id))}></i>}
            <p
              id="suggestion-p"
              onClick={() => {
                history.push(`/Results?q=${encodeURIComponent(suggestion._id)}&page=1&limit=${+process.env.REACT_APP_RESULTS_PER_PAGE}`);
              }}
            >
              {suggestion._id.length > 25 ? suggestion._id.slice(0, 25) + "..." : suggestion._id}
            </p>
          </div>
        );
      })}
    </div>
  );
};

export default Suggestions;
