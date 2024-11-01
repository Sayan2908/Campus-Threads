import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { SubredditModel } from '../subreddit-response';
import { Router } from '@angular/router';
import { SubredditService } from '../subreddit.service';
import { throwError } from 'rxjs';

@Component({
  selector: 'app-create-subreddit',
  templateUrl: './create-subreddit.component.html',
  styleUrls: ['./create-subreddit.component.css']
})
export class CreateSubredditComponent implements OnInit {
  createSubredditForm: FormGroup;
  subredditModel: SubredditModel;
  title = new FormControl('');
  description = new FormControl('');

  constructor(private router: Router, private subredditService: SubredditService) {
    this.createSubredditForm = new FormGroup({
      title: new FormControl('', Validators.required),
      description: new FormControl('', Validators.required)
    });
    this.subredditModel = {
      name: '',
      description: ''
    }
  }

  ngOnInit() {
  }

  discard() {
    this.router.navigateByUrl('/');
  }

  createSubreddit() {
    var titleControl =  this.createSubredditForm.get('title');
    var descriptionControl = this.createSubredditForm.get('description');
    if (titleControl !== null && descriptionControl !== null) {
    this.subredditModel.name = titleControl.value;
    this.subredditModel.description = descriptionControl.value;
    }
    this.subredditService.createSubreddit(this.subredditModel).subscribe(data => {
      this.router.navigateByUrl('/list-threads');
    }, error => {
      throwError(error);
    })
  }
}
