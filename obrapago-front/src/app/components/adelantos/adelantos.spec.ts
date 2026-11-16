import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { AdelantosComponent } from './adelantos';

describe('AdelantosComponent', () => {
  let component: AdelantosComponent;
  let fixture: ComponentFixture<AdelantosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdelantosComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AdelantosComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
