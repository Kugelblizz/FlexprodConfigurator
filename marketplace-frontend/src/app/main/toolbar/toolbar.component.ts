import { Component, ViewChild, ElementRef } from '@angular/core';
import { NavigationEnd, NavigationStart, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

import { FuseConfigService } from '@fuse/services/config.service';
import { FuseSidebarService } from '@fuse/components/sidebar/sidebar.service';

import { navigation_volunteer } from 'app/navigation/navigation_volunteer';
import { navigation_helpseeker } from '../../navigation/navigation_helpseeker';
import { LoginService } from '../content/_service/login.service';
import { Participant, ParticipantRole } from '../content/_model/participant';

@Component({
  selector: 'fuse-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss']
})

export class FuseToolbarComponent {
  userStatusOptions: any[];
  languages: any;
  selectedLanguage: any;
  showLoadingBar: boolean;
  horizontalNav: boolean;
  noNav: boolean;
  navigation: any;
  icons: string;

  @ViewChild('inboxIcon', {static: true}) inboxIcon: ElementRef;
  displayInboxOverlay: boolean;

  constructor(private router: Router,
    private fuseConfig: FuseConfigService,
    private loginService: LoginService,
    private sidebarService: FuseSidebarService,
    private translate: TranslateService) {
    this.userStatusOptions = [
      {
        'title': 'Online',
        'icon': 'icon-checkbox-marked-circle',
        'color': '#4CAF50'
      },
      {
        'title': 'Away',
        'icon': 'icon-clock',
        'color': '#FFC107'
      },
      {
        'title': 'Do not Disturb',
        'icon': 'icon-minus-circle',
        'color': '#F44336'
      },
      {
        'title': 'Invisible',
        'icon': 'icon-checkbox-blank-circle-outline',
        'color': '#BDBDBD'
      },
      {
        'title': 'Offline',
        'icon': 'icon-checkbox-blank-circle-outline',
        'color': '#616161'
      }
    ];

    this.languages = [
      {
        'id': 'en',
        'title': 'English',
        'flag': 'us'
      },
      {
        'id': 'tr',
        'title': 'Turkish',
        'flag': 'tr'
      }
    ];

    this.selectedLanguage = this.languages[0];

    router.events.subscribe(
      (event) => {
        if (event instanceof NavigationStart) {
          this.showLoadingBar = true;
        }
        if (event instanceof NavigationEnd) {
          this.showLoadingBar = false;
        }
      });

    this.fuseConfig.onConfigChanged.subscribe((settings) => {
      this.horizontalNav = settings.layout.navigation === 'top';
      this.noNav = settings.layout.navigation === 'none';
    });

    this.loginService.getLoggedInParticipantRole().toPromise().then((role: ParticipantRole) => {
      switch (role) {
        case 'HELP_SEEKER':
          this.navigation = navigation_helpseeker;
          this.icons = 'HELP_SEEKER';
          break;
        case 'VOLUNTEER':
          this.navigation = navigation_volunteer;
          this.icons = 'VOLUNTEER';
          break;
      }
    }).catch(e => {
      console.warn(e);
    });
  }

  toggleSidebarOpened(key) {
    this.sidebarService.getSidebar(key).toggleOpen();
  }

  search(value) {
    // Do your search here...
    console.log(value);
  }


  toggleInboxOverlay(event: any, inboxIcon: any) {
    console.log(event);
    this.displayInboxOverlay = !this.displayInboxOverlay;
    console.log();
    
  }


  setLanguage(lang) {
    // Set the selected language for toolbar
    this.selectedLanguage = lang;

    // Use the selected language for translations
    this.translate.use(lang.id);
  }
}
