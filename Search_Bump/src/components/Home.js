//Speech recognition code from https://github.com/Riley-Brown/react-speech-to-text
import React, { useState, useEffect } from "react";
import useSpeechToText from "react-hook-speech-to-text";
import { useHistory } from "react-router-dom";
import "./Home.scss";
import { getSuggestions } from "../API/suggestions";
import { addToSearchHistory, getSearchHistory } from "../helpers/userSearchHistory";
import Suggestions from "./Suggestions";
import useOuterClick from "../helpers/useOuterClick";

const Home = () => {
  let history = useHistory();
  const [suggestions, setSuggestions] = useState(getSearchHistory());
  const [loadingSuggestions, setLoadingSuggestions] = useState(false);
  const [searchInput, setSearchInput] = useState("");
  const [searchFocused, setSearchFocused] = useState(false);
  const [showSuggestionsAfterDeletion, setShowSuggestionsAfterDeletion] = useState(false);

  //speech recognition
  const { error, isRecording, results, startSpeechToText, stopSpeechToText, interimResult } = useSpeechToText({
    continuous: false,
    crossBrowser: true,
    timeout: 10000,
    speechRecognitionProperties: { interimResults: true },
  });

  //update the search input value when voice recording is used
  useEffect(() => {
    console.log(interimResult);
    if (isRecording && interimResult) {
      document.getElementById("home-input").value = interimResult;
      setSearchInput(interimResult);
    }
  }, [interimResult]);

  //if the search input changed due to voice, automatically search
  useEffect(() => {
    console.log(interimResult);
    if (!isRecording && searchInput != "") {
      search();
    }
  }, [isRecording]);

  //when clicking on the search button
  const search = () => {
    //e.preventDefault();
    if (searchInput !== "") {
      addToSearchHistory(searchInput);
      history.push(`/Results?q=${encodeURIComponent(searchInput)}&page=1&limit=${+process.env.REACT_APP_RESULTS_PER_PAGE}`);
    }
  };

  //when pressing enter in the search field
  const searchEnter = (e) => {
    if (e.keyCode === 13) {
      if (searchInput !== "") {
        addToSearchHistory(searchInput);
        history.push(`/Results?q=${encodeURIComponent(searchInput)}&page=1&limit=${+process.env.REACT_APP_RESULTS_PER_PAGE}`);
      }
    }
  };

  //To get the history of all users as suggestions
  const handleInputChange = async (e) => {
    e.preventDefault();
    var query = document.getElementById("home-input").value;
    console.log(query);
    setSearchInput(query);
    if (query.trim() !== "") {
      setLoadingSuggestions(true);
      try {
        setSuggestions(await getSuggestions(query));
      } catch (e) {
        console.log(e);
      } finally {
        setLoadingSuggestions(false);
      }
    } else {
      setSuggestions(getSearchHistory());
    }
  };

  const insideSuggestions = useOuterClick((ev) => {
    setSearchFocused(false);
  });

  const updateDeletedSearchHistory = (newSearchHistory) => {
    setSuggestions(newSearchHistory);
    setShowSuggestionsAfterDeletion(true);
  };
  useEffect(() => {
    setSearchFocused(showSuggestionsAfterDeletion && true);
  }, [showSuggestionsAfterDeletion]);

  return (
    <div className="home-body">
      <div className="search-section center-me">
        <img src={require('./SearchBump.png').default} width="662px" height="280px" id="home-logo" alt="Search Bump"></img>
        <div ref={insideSuggestions} className="d-flex align-items-center justify-content-center" style={{ position: "relative" }}>
          <button id="voice" className={"fas fa-microphone-alt " + (isRecording ? "glow" : "")} onClick={isRecording ? stopSpeechToText : startSpeechToText}></button>
          <input
            id="home-input"
            type="text"
            className="form-control"
            placeholder="Type Here..."
            onKeyDown={searchEnter}
            onChange={handleInputChange}
            //value={interimResult}
            autoComplete="off"
            //style={{ width: "100%" }}
            onFocus={() => setSearchFocused(true)}
          ></input>
          <button className="fas fa-search search-button" onClick={search} style={{ zIndex: 200 }}></button>
          {searchFocused && !loadingSuggestions && suggestions?.length !== 0 && (
            <Suggestions suggestions={suggestions} searchInput={searchInput} styles="suggestion-item" marg="-1px" width="50%" onDeleteFromHistory={updateDeletedSearchHistory} />
          )}
        </div>
      </div>
    </div>
  );
};

export default Home;
