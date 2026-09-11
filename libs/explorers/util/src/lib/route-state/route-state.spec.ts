import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { render } from '@testing-library/angular';
import { routeSnapshotChanges } from './route-state';

interface RouteStateEmission {
  name: string | null;
  query: string | null;
}

const emissions: RouteStateEmission[] = [];

@Component({
  selector: 'explorers-route-state-probe',
  template: '',
})
class RouteStateProbeComponent {
  constructor() {
    routeSnapshotChanges(inject(ActivatedRoute)).subscribe((snapshot) => {
      emissions.push({
        name: snapshot.paramMap.get('name'),
        query: snapshot.queryParamMap.get('q'),
      });
    });
  }
}

async function setup() {
  emissions.length = 0;

  return render('<router-outlet></router-outlet>', {
    imports: [RouterOutlet],
    routes: [{ path: 'items/:name', component: RouteStateProbeComponent }],
    initialRoute: '/items/A?q=1',
  });
}

describe('routeSnapshotChanges', () => {
  it('should emit once per navigation, with params and query params of the same navigation', async () => {
    const { navigate } = await setup();
    expect(emissions).toEqual([{ name: 'A', query: '1' }]);

    await navigate('/items/B?q=2');

    expect(emissions).toEqual([
      { name: 'A', query: '1' },
      { name: 'B', query: '2' },
    ]);
  });

  it('should emit when only the query params change', async () => {
    const { navigate } = await setup();

    await navigate('/items/A?q=2');

    expect(emissions).toEqual([
      { name: 'A', query: '1' },
      { name: 'A', query: '2' },
    ]);
  });
});
