import React ,{ Component} from 'react';
import "./SearchResult.scss"

export class SearchResult extends Component {

  render() {

    return (

      <div className="info contaier">
      
        <div> 
          <a className="url" href={this.props.URL}> {this.props.URL} </a>
          <div> 
            <a className="Title" href={this.props.URL}> {this.props.pageTitle} | {this.props.siteName} </a>
          </div>
          <div className="preview"> {this.props.preview} </div>
        </div>

      </div>

    )
  };

};

export default SearchResult;